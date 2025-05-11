package ai.magicdb.dataservice.core.monitoring

import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.MonitoringRepository
import ai.magicdb.dataservice.api.MonitoringService
import ai.magicdb.dataservice.api.model.ServiceCallRecord
import ai.magicdb.dataservice.api.model.ServiceCallStatistics
import ai.magicdb.dataservice.api.model.ServiceErrorStatistics
import ai.magicdb.dataservice.api.model.ServicePerformanceMetrics
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

/**
 * 默认监控服务实现
 *
 * @author magicdb
 */
@Service
class DefaultMonitoringService(
    private val monitoringRepository: MonitoringRepository,
    private val dataServiceRepository: DataServiceRepository
) : MonitoringService {
    private val logger = LoggerFactory.getLogger(DefaultMonitoringService::class.java)

    override fun recordServiceCall(
        serviceId: String,
        executionTime: Long,
        success: Boolean,
        errorMessage: String?,
        userId: Long?,
        clientIp: String?
    ) {
        try {
            // 获取服务信息
            val service = dataServiceRepository.getService(serviceId)

            // 创建调用记录
            val record = ServiceCallRecord(
                id = UUID.randomUUID().toString(),
                serviceId = serviceId,
                serviceName = service?.name ?: "Unknown",
                callTime = LocalDateTime.now(),
                executionTime = executionTime,
                success = success,
                errorMessage = errorMessage,
                errorType = if (errorMessage != null) extractErrorType(errorMessage) else null,
                userId = userId,
                clientIp = clientIp
            )

            // 保存调用记录
            monitoringRepository.saveCallRecord(record)
        } catch (e: Exception) {
            logger.error("记录服务调用失败: {}", serviceId, e)
        }
    }

    override fun getServiceCallStatistics(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<ServiceCallStatistics> {
        try {
            // 获取服务ID列表
            val serviceIds = if (serviceId != null) {
                listOf(serviceId)
            } else {
                // 获取所有有调用记录的服务ID
                monitoringRepository.getCallRecords(
                    startTime = startTime,
                    endTime = endTime,
                    limit = Int.MAX_VALUE
                ).map { it.serviceId }.distinct()
            }

            // 获取每个服务的统计信息
            return serviceIds.map { id ->
                getServiceStatistics(id, startTime, endTime)
            }
        } catch (e: Exception) {
            logger.error("获取服务调用统计失败", e)
            return emptyList()
        }
    }

    override fun getServiceErrorStatistics(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<ServiceErrorStatistics> {
        try {
            // 获取服务ID列表
            val serviceIds = if (serviceId != null) {
                listOf(serviceId)
            } else {
                // 获取所有有错误记录的服务ID
                monitoringRepository.getCallRecords(
                    startTime = startTime,
                    endTime = endTime,
                    success = false,
                    limit = Int.MAX_VALUE
                ).map { it.serviceId }.distinct()
            }

            // 获取每个服务的错误统计信息
            return serviceIds.map { id ->
                getServiceErrorStats(id, startTime, endTime)
            }
        } catch (e: Exception) {
            logger.error("获取服务错误统计失败", e)
            return emptyList()
        }
    }

    override fun getServicePerformanceMetrics(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<ServicePerformanceMetrics> {
        try {
            // 获取服务ID列表
            val serviceIds = if (serviceId != null) {
                listOf(serviceId)
            } else {
                // 获取所有有调用记录的服务ID
                monitoringRepository.getCallRecords(
                    startTime = startTime,
                    endTime = endTime,
                    limit = Int.MAX_VALUE
                ).map { it.serviceId }.distinct()
            }

            // 获取每个服务的性能指标
            return serviceIds.map { id ->
                getServicePerformance(id, startTime, endTime)
            }
        } catch (e: Exception) {
            logger.error("获取服务性能指标失败", e)
            return emptyList()
        }
    }

    override fun getRecentlyCalledServices(limit: Int): List<String> {
        try {
            return monitoringRepository.getRecentlyCalledServices(limit)
        } catch (e: Exception) {
            logger.error("获取最近调用的服务失败", e)
            return emptyList()
        }
    }

    override fun getMostCalledServices(
        limit: Int,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<String> {
        try {
            return monitoringRepository.getMostCalledServices(limit, startTime, endTime)
                .map { it.first }
        } catch (e: Exception) {
            logger.error("获取调用最多的服务失败", e)
            return emptyList()
        }
    }

    override fun getMostErrorServices(
        limit: Int,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<String> {
        try {
            return monitoringRepository.getMostErrorServices(limit, startTime, endTime)
                .map { it.first }
        } catch (e: Exception) {
            logger.error("获取错误最多的服务失败", e)
            return emptyList()
        }
    }

    override fun getWorstPerformanceServices(
        limit: Int,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<String> {
        try {
            return monitoringRepository.getWorstPerformanceServices(limit, startTime, endTime)
                .map { it.first }
        } catch (e: Exception) {
            logger.error("获取性能最差的服务失败", e)
            return emptyList()
        }
    }

    override fun clearMonitoringData(
        serviceId: String?,
        before: LocalDateTime?
    ): Int {
        try {
            return monitoringRepository.clearMonitoringData(serviceId, before)
        } catch (e: Exception) {
            logger.error("清除监控数据失败", e)
            return 0
        }
    }

    /**
     * 获取服务统计信息
     */
    private fun getServiceStatistics(
        serviceId: String,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): ServiceCallStatistics {
        // 获取服务信息
        val service = dataServiceRepository.getService(serviceId)
        val serviceName = service?.name ?: "Unknown"

        // 获取调用次数
        val totalCalls = monitoringRepository.getCallRecordCount(serviceId, startTime, endTime)
        val successCalls = monitoringRepository.getSuccessCallCount(serviceId, startTime, endTime)
        val failedCalls = monitoringRepository.getFailedCallCount(serviceId, startTime, endTime)

        // 计算成功率
        val successRate = if (totalCalls > 0) {
            successCalls.toDouble() / totalCalls
        } else {
            0.0
        }

        // 获取执行时间
        val avgExecutionTime = monitoringRepository.getAvgExecutionTime(serviceId, startTime, endTime)
        val maxExecutionTime = monitoringRepository.getMaxExecutionTime(serviceId, startTime, endTime)
        val minExecutionTime = monitoringRepository.getMinExecutionTime(serviceId, startTime, endTime)

        // 获取最后调用时间
        val lastCalledTime = monitoringRepository.getLastCallTime(serviceId) ?: LocalDateTime.now()

        // 获取用户和IP数
        val uniqueUsers = monitoringRepository.getUniqueUserCount(serviceId, startTime, endTime)
        val uniqueIps = monitoringRepository.getUniqueIpCount(serviceId, startTime, endTime)

        // 获取按时间统计的调用次数
        val callsByHour = monitoringRepository.getCallsByHour(serviceId, startTime, endTime)
        val callsByDay = monitoringRepository.getCallsByDay(serviceId, startTime, endTime)

        // 创建统计信息
        return ServiceCallStatistics(
            serviceId = serviceId,
            serviceName = serviceName,
            totalCalls = totalCalls,
            successCalls = successCalls,
            failedCalls = failedCalls,
            successRate = successRate,
            avgExecutionTime = avgExecutionTime,
            maxExecutionTime = maxExecutionTime,
            minExecutionTime = minExecutionTime,
            lastCalledTime = lastCalledTime,
            startTime = startTime ?: LocalDateTime.MIN,
            endTime = endTime ?: LocalDateTime.now(),
            uniqueUsers = uniqueUsers,
            uniqueIps = uniqueIps,
            callsByHour = callsByHour,
            callsByDay = callsByDay
        )
    }

    /**
     * 获取服务错误统计信息
     */
    private fun getServiceErrorStats(
        serviceId: String,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): ServiceErrorStatistics {
        // 获取服务信息
        val service = dataServiceRepository.getService(serviceId)
        val serviceName = service?.name ?: "Unknown"

        // 获取调用次数
        val totalCalls = monitoringRepository.getCallRecordCount(serviceId, startTime, endTime)
        val failedCalls = monitoringRepository.getFailedCallCount(serviceId, startTime, endTime)

        // 计算错误率
        val errorRate = if (totalCalls > 0) {
            failedCalls.toDouble() / totalCalls
        } else {
            0.0
        }

        // 获取错误类型统计
        val errorTypes = monitoringRepository.getErrorTypeStats(serviceId, startTime, endTime)

        // 获取最常见的错误消息
        val mostCommonErrors = monitoringRepository.getMostCommonErrors(serviceId, startTime, endTime)

        // 获取最后错误时间
        val lastErrorTime = monitoringRepository.getCallRecords(
            serviceId = serviceId,
            startTime = startTime,
            endTime = endTime,
            success = false,
            limit = 1
        ).firstOrNull()?.callTime ?: LocalDateTime.now()

        // 获取按时间统计的错误次数
        val errorsByHour = monitoringRepository.getErrorsByHour(serviceId, startTime, endTime)
        val errorsByDay = monitoringRepository.getErrorsByDay(serviceId, startTime, endTime)

        // 创建错误统计信息
        return ServiceErrorStatistics(
            serviceId = serviceId,
            serviceName = serviceName,
            totalErrors = failedCalls,
            errorRate = errorRate,
            errorTypes = errorTypes,
            mostCommonErrors = mostCommonErrors,
            lastErrorTime = lastErrorTime,
            startTime = startTime ?: LocalDateTime.MIN,
            endTime = endTime ?: LocalDateTime.now(),
            errorsByHour = errorsByHour,
            errorsByDay = errorsByDay
        )
    }

    /**
     * 获取服务性能指标
     */
    private fun getServicePerformance(
        serviceId: String,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): ServicePerformanceMetrics {
        // 获取服务信息
        val service = dataServiceRepository.getService(serviceId)
        val serviceName = service?.name ?: "Unknown"

        // 获取响应时间
        val avgResponseTime = monitoringRepository.getAvgExecutionTime(serviceId, startTime, endTime, true)
        val maxResponseTime = monitoringRepository.getMaxExecutionTime(serviceId, startTime, endTime, true)
        val minResponseTime = monitoringRepository.getMinExecutionTime(serviceId, startTime, endTime, true)

        // 获取调用次数和时间范围
        val totalCalls = monitoringRepository.getCallRecordCount(serviceId, startTime, endTime)
        val actualStartTime = startTime ?: monitoringRepository.getCallRecords(
            serviceId = serviceId,
            limit = 1,
            offset = 0
        ).firstOrNull()?.callTime ?: LocalDateTime.now()
        val actualEndTime = endTime ?: LocalDateTime.now()

        // 计算时间范围（秒）
        val timeRangeSeconds = java.time.Duration.between(actualStartTime, actualEndTime).seconds

        // 计算每秒请求数
        val requestsPerSecond = if (timeRangeSeconds > 0) {
            totalCalls.toDouble() / timeRangeSeconds
        } else {
            0.0
        }

        // 获取并发用户数（简化为唯一用户数）
        val concurrentUsers = monitoringRepository.getUniqueUserCount(serviceId, startTime, endTime)

        // 创建性能指标
        return ServicePerformanceMetrics(
            serviceId = serviceId,
            serviceName = serviceName,
            avgResponseTime = avgResponseTime,
            maxResponseTime = maxResponseTime,
            minResponseTime = minResponseTime,
            percentiles = mapOf(
                "50th" to calculatePercentile(serviceId, startTime, endTime, 50),
                "90th" to calculatePercentile(serviceId, startTime, endTime, 90),
                "95th" to calculatePercentile(serviceId, startTime, endTime, 95),
                "99th" to calculatePercentile(serviceId, startTime, endTime, 99)
            ),
            requestsPerSecond = requestsPerSecond,
            concurrentUsers = concurrentUsers,
            memoryUsage = 0.0, // 暂不实现
            cpuUsage = 0.0, // 暂不实现
            startTime = actualStartTime,
            endTime = actualEndTime,
            responseTimeByTimeSlot = calculateResponseTimeByTimeSlot(serviceId, startTime, endTime),
            requestsByTimeSlot = calculateRequestsByTimeSlot(serviceId, startTime, endTime)
        )
    }

    /**
     * 计算百分位数
     */
    private fun calculatePercentile(
        serviceId: String,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        percentile: Int
    ): Double {
        // 获取所有成功调用的执行时间
        val executionTimes = monitoringRepository.getCallRecords(
            serviceId = serviceId,
            startTime = startTime,
            endTime = endTime,
            success = true,
            limit = Int.MAX_VALUE
        ).map { it.executionTime }.sorted()

        // 计算百分位数
        if (executionTimes.isEmpty()) {
            return 0.0
        }

        val index = (executionTimes.size * percentile / 100.0).toInt()
        return executionTimes.getOrElse(index) { executionTimes.last() }.toDouble()
    }

    /**
     * 计算按时间段的平均响应时间
     */
    private fun calculateResponseTimeByTimeSlot(
        serviceId: String,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Map<String, Double> {
        // 获取所有成功调用记录
        val records = monitoringRepository.getCallRecords(
            serviceId = serviceId,
            startTime = startTime,
            endTime = endTime,
            success = true,
            limit = Int.MAX_VALUE
        )

        // 按小时分组
        return records.groupBy { "${it.callTime.toLocalDate()} ${it.callTime.hour}:00" }
            .mapValues { (_, records) -> records.map { it.executionTime }.average() }
    }

    /**
     * 计算按时间段的请求数
     */
    private fun calculateRequestsByTimeSlot(
        serviceId: String,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Map<String, Int> {
        // 获取所有调用记录
        val records = monitoringRepository.getCallRecords(
            serviceId = serviceId,
            startTime = startTime,
            endTime = endTime,
            limit = Int.MAX_VALUE
        )

        // 按小时分组
        return records.groupBy { "${it.callTime.toLocalDate()} ${it.callTime.hour}:00" }
            .mapValues { (_, records) -> records.size }
    }

    /**
     * 提取错误类型
     */
    private fun extractErrorType(errorMessage: String): String {
        // 简单实现：提取第一个冒号前的内容，或者第一个空格前的内容
        val colonIndex = errorMessage.indexOf(':')
        if (colonIndex > 0) {
            return errorMessage.substring(0, colonIndex).trim()
        }

        val spaceIndex = errorMessage.indexOf(' ')
        if (spaceIndex > 0) {
            return errorMessage.substring(0, spaceIndex).trim()
        }

        // 如果没有冒号或空格，返回整个消息（最多30个字符）
        return errorMessage.take(30).trim()
    }
}
