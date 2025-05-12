package ai.magicdb.dataservice.core.performance

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.model.ServiceResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.annotation.PreDestroy

/**
 * 压力测试工具
 * 用于模拟高负载场景
 *
 * @author magicdb
 */
@Component
class StressTestTool(
    private val dataServiceRepository: DataServiceRepository,
    private val dataServiceExecutor: DataServiceExecutor,
    private val performanceCollector: PerformanceCollector
) {
    private val logger = LoggerFactory.getLogger(StressTestTool::class.java)

    // 线程池
    private val executor = Executors.newCachedThreadPool()

    // 测试任务
    private val testTasks = ConcurrentHashMap<String, TestTask>()

    /**
     * 销毁
     */
    @PreDestroy
    fun destroy() {
        // 停止所有测试
        stopAllTests()

        // 关闭线程池
        executor.shutdown()
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
        }

        logger.info("压力测试工具已销毁")
    }

    /**
     * 启动压力测试
     *
     * @param serviceId 服务ID
     * @param parameters 参数
     * @param concurrentUsers 并发用户数
     * @param duration 持续时间（秒）
     * @param rampUp 爬升时间（秒）
     * @return 测试ID
     */
    fun startTest(
        serviceId: String,
        parameters: Map<String, Any?>,
        concurrentUsers: Int,
        duration: Int,
        rampUp: Int
    ): String {
        // 检查服务是否存在
        val service = dataServiceRepository.getService(serviceId)
        if (service == null) {
            throw IllegalArgumentException("服务不存在: $serviceId")
        }

        // 创建测试ID
        val testId = UUID.randomUUID().toString()

        // 创建测试任务
        val testTask = TestTask(
            id = testId,
            serviceId = serviceId,
            parameters = parameters,
            concurrentUsers = concurrentUsers,
            duration = duration,
            rampUp = rampUp,
            startTime = LocalDateTime.now()
        )

        // 保存测试任务
        testTasks[testId] = testTask

        // 启动测试
        startTestTask(testTask)

        logger.info("启动压力测试: {}, 服务: {}, 并发用户: {}, 持续时间: {}秒", testId, serviceId, concurrentUsers, duration)

        return testId
    }

    /**
     * 停止压力测试
     *
     * @param testId 测试ID
     * @return 是否成功
     */
    fun stopTest(testId: String): Boolean {
        val testTask = testTasks[testId]
        if (testTask != null) {
            // 停止测试
            testTask.running.set(false)

            // 更新结束时间
            testTask.endTime = LocalDateTime.now()

            logger.info("停止压力测试: {}", testId)

            return true
        }

        return false
    }

    /**
     * 停止所有测试
     */
    fun stopAllTests() {
        testTasks.keys.forEach { testId ->
            stopTest(testId)
        }
    }

    /**
     * 获取测试状态
     *
     * @param testId 测试ID
     * @return 测试状态
     */
    fun getTestStatus(testId: String): TestStatus? {
        val testTask = testTasks[testId] ?: return null

        return TestStatus(
            id = testTask.id,
            serviceId = testTask.serviceId,
            concurrentUsers = testTask.concurrentUsers,
            duration = testTask.duration,
            rampUp = testTask.rampUp,
            startTime = testTask.startTime,
            endTime = testTask.endTime,
            running = testTask.running.get(),
            totalRequests = testTask.totalRequests.get(),
            successRequests = testTask.successRequests.get(),
            failedRequests = testTask.failedRequests.get(),
            totalTime = testTask.totalTime.get(),
            minTime = testTask.minTime.get(),
            maxTime = testTask.maxTime.get(),
            avgTime = if (testTask.totalRequests.get() > 0) {
                testTask.totalTime.get() / testTask.totalRequests.get()
            } else {
                0
            },
            requestsPerSecond = if (testTask.running.get()) {
                val elapsedSeconds = (System.currentTimeMillis() - testTask.startTime.toEpochSecond(java.time.ZoneOffset.UTC) * 1000) / 1000
                if (elapsedSeconds > 0) {
                    testTask.totalRequests.get().toDouble() / elapsedSeconds
                } else {
                    0.0
                }
            } else {
                val elapsedSeconds = (testTask.endTime?.toEpochSecond(java.time.ZoneOffset.UTC) ?: System.currentTimeMillis() / 1000) -
                        testTask.startTime.toEpochSecond(java.time.ZoneOffset.UTC)
                if (elapsedSeconds > 0) {
                    testTask.totalRequests.get().toDouble() / elapsedSeconds
                } else {
                    0.0
                }
            },
            errorRate = if (testTask.totalRequests.get() > 0) {
                testTask.failedRequests.get().toDouble() / testTask.totalRequests.get()
            } else {
                0.0
            }
        )
    }

    /**
     * 获取所有测试状态
     *
     * @return 所有测试状态
     */
    fun getAllTestStatus(): List<TestStatus> {
        return testTasks.keys.mapNotNull { testId ->
            getTestStatus(testId)
        }
    }

    /**
     * 清除测试历史
     *
     * @param testId 测试ID
     * @return 是否成功
     */
    fun clearTestHistory(testId: String): Boolean {
        val testTask = testTasks[testId]
        if (testTask != null && !testTask.running.get()) {
            testTasks.remove(testId)
            return true
        }

        return false
    }

    /**
     * 清除所有测试历史
     */
    fun clearAllTestHistory() {
        val completedTestIds = testTasks.entries
            .filter { !it.value.running.get() }
            .map { it.key }

        completedTestIds.forEach { testId ->
            testTasks.remove(testId)
        }
    }

    /**
     * 启动测试任务
     *
     * @param testTask 测试任务
     */
    private fun startTestTask(testTask: TestTask) {
        // 设置运行状态
        testTask.running.set(true)

        // 提交测试任务
        CompletableFuture.runAsync({
            try {
                // 计算每个用户启动间隔
                val userStartInterval = if (testTask.rampUp > 0 && testTask.concurrentUsers > 1) {
                    (testTask.rampUp * 1000) / (testTask.concurrentUsers - 1)
                } else {
                    0
                }

                // 启动用户线程
                val userFutures = (0 until testTask.concurrentUsers).map { userId ->
                    CompletableFuture.runAsync({
                        // 等待爬升时间
                        if (userId > 0 && userStartInterval > 0) {
                            Thread.sleep(userId * userStartInterval.toLong())
                        }

                        // 执行用户请求
                        executeUserRequests(testTask)
                    }, executor)
                }

                // 等待所有用户线程完成
                CompletableFuture.allOf(*userFutures.toTypedArray()).join()
            } catch (e: Exception) {
                logger.error("执行压力测试失败: {}", testTask.id, e)
            } finally {
                // 设置结束时间
                testTask.endTime = LocalDateTime.now()

                // 设置运行状态
                testTask.running.set(false)

                logger.info("压力测试完成: {}", testTask.id)
            }
        }, executor)
    }

    /**
     * 执行用户请求
     *
     * @param testTask 测试任务
     */
    private fun executeUserRequests(testTask: TestTask) {
        // 计算结束时间
        val endTimeMillis = System.currentTimeMillis() + testTask.duration * 1000L

        // 执行请求
        while (testTask.running.get() && System.currentTimeMillis() < endTimeMillis) {
            try {
                // 执行服务
                val startTime = System.currentTimeMillis()
                val result = dataServiceExecutor.execute(testTask.serviceId, testTask.parameters)
                val endTime = System.currentTimeMillis()

                // 计算执行时间
                val executionTime = endTime - startTime

                // 更新统计信息
                testTask.totalRequests.incrementAndGet()
                testTask.totalTime.addAndGet(executionTime)

                // 更新最小执行时间
                var minTime = testTask.minTime.get()
                while (minTime == 0L || executionTime < minTime) {
                    if (testTask.minTime.compareAndSet(minTime, executionTime)) {
                        break
                    }
                    minTime = testTask.minTime.get()
                }

                // 更新最大执行时间
                var maxTime = testTask.maxTime.get()
                while (executionTime > maxTime) {
                    if (testTask.maxTime.compareAndSet(maxTime, executionTime)) {
                        break
                    }
                    maxTime = testTask.maxTime.get()
                }

                // 检查结果
                if (result.success) {
                    testTask.successRequests.incrementAndGet()
                } else {
                    testTask.failedRequests.incrementAndGet()
                }

                // 记录请求
                performanceCollector.recordRequest()
            } catch (e: Exception) {
                // 更新统计信息
                testTask.totalRequests.incrementAndGet()
                testTask.failedRequests.incrementAndGet()

                logger.error("执行服务失败: {}", testTask.serviceId, e)
            }

            // 随机等待一段时间（模拟用户思考时间）
            try {
                Thread.sleep((Math.random() * 1000).toLong())
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }
    }

    /**
     * 测试任务
     */
    data class TestTask(
        val id: String,
        val serviceId: String,
        val parameters: Map<String, Any?>,
        val concurrentUsers: Int,
        val duration: Int,
        val rampUp: Int,
        val startTime: LocalDateTime,
        var endTime: LocalDateTime? = null,
        val running: AtomicBoolean = AtomicBoolean(false),
        val totalRequests: AtomicLong = AtomicLong(0),
        val successRequests: AtomicLong = AtomicLong(0),
        val failedRequests: AtomicLong = AtomicLong(0),
        val totalTime: AtomicLong = AtomicLong(0),
        val minTime: AtomicLong = AtomicLong(0),
        val maxTime: AtomicLong = AtomicLong(0)
    )

    /**
     * 测试状态
     */
    data class TestStatus(
        val id: String,
        val serviceId: String,
        val concurrentUsers: Int,
        val duration: Int,
        val rampUp: Int,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime?,
        val running: Boolean,
        val totalRequests: Long,
        val successRequests: Long,
        val failedRequests: Long,
        val totalTime: Long,
        val minTime: Long,
        val maxTime: Long,
        val avgTime: Long,
        val requestsPerSecond: Double,
        val errorRate: Double
    )
}
