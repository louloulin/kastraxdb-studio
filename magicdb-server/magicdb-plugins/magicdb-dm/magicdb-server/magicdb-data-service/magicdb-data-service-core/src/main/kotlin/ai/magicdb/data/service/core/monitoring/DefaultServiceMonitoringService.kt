package ai.magicdb.data.service.core.monitoring

import ai.magicdb.data.service.api.DataServiceManager
import ai.magicdb.data.service.api.ServiceMonitoringService
import ai.magicdb.data.service.api.model.ErrorStatistics
import ai.magicdb.data.service.api.model.ExecutionRecord
import ai.magicdb.data.service.api.model.ServiceStatistics
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 默认服务监控实现
 */
@Service
class DefaultServiceMonitoringService(
    private val dataServiceManager: DataServiceManager
) : ServiceMonitoringService {

    private val logger = LoggerFactory.getLogger(DefaultServiceMonitoringService::class.java)

    // 执行记录存储
    private val executionRecords = CopyOnWriteArrayList<ExecutionRecord>()

    // 服务统计缓存
    private val serviceStatisticsCache = ConcurrentHashMap<String, ServiceStatistics>()

    // 错误统计缓存
    private val errorStatisticsCache = ConcurrentHashMap<String, MutableList<ErrorStatistics>>()

    // 最大记录数
    private val maxRecords = 10000

    override fun recordExecution(serviceId: String, executionTime: Long, success: Boolean, errorMessage: String?) {
        try {
            // 获取服务
            val service = dataServiceManager.getService(serviceId)

            if (service == null) {
                logger.warn("记录执行统计失败：服务不存在 {}", serviceId)
                return
            }

            // 创建执行记录
            val record = ExecutionRecord(
                id = UUID.randomUUID().toString(),
                serviceId = serviceId,
                serviceName = service.name,
                executionTime = executionTime,
                success = success,
                errorMessage = errorMessage,
                timestamp = Date()
            )

            // 添加记录
            executionRecords.add(0, record)

            // 如果记录数超过最大值，删除旧记录
            if (executionRecords.size > maxRecords) {
                executionRecords.subList(maxRecords, executionRecords.size).clear()
            }

            // 更新服务统计
            updateServiceStatistics(record)

            // 如果执行失败，更新错误统计
            if (!success && errorMessage != null) {
                updateErrorStatistics(record)
            }
        } catch (e: Exception) {
            logger.error("记录执行统计失败", e)
        }
    }

    override fun getServiceStatistics(
        serviceId: String?,
        startTime: Date?,
        endTime: Date?
    ): List<ServiceStatistics> {
        // 如果指定了服务ID，返回该服务的统计
        if (serviceId != null) {
            val statistics = serviceStatisticsCache[serviceId]
            return if (statistics != null) listOf(statistics) else emptyList()
        }

        // 否则返回所有服务的统计
        return serviceStatisticsCache.values.toList()
    }

    override fun getExecutionHistory(
        serviceId: String?,
        startTime: Date?,
        endTime: Date?,
        limit: Int,
        offset: Int
    ): List<ExecutionRecord> {
        // 筛选记录
        val filteredRecords = executionRecords.filter { record ->
            (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || record.timestamp.after(startTime) || record.timestamp == startTime) &&
                (endTime == null || record.timestamp.before(endTime) || record.timestamp == endTime)
        }

        // 分页
        val endIndex = (offset + limit).coerceAtMost(filteredRecords.size)
        return if (offset < filteredRecords.size) {
            filteredRecords.subList(offset, endIndex)
        } else {
            emptyList()
        }
    }

    override fun getErrorStatistics(
        serviceId: String?,
        startTime: Date?,
        endTime: Date?
    ): List<ErrorStatistics> {
        // 如果指定了服务ID，返回该服务的错误统计
        if (serviceId != null) {
            val statistics = errorStatisticsCache[serviceId] ?: emptyList()
            return statistics.filter { stats ->
                (startTime == null || stats.lastOccurrence.after(startTime) || stats.lastOccurrence == startTime) &&
                    (endTime == null || stats.firstOccurrence.before(endTime) || stats.firstOccurrence == endTime)
            }
        }

        // 否则返回所有服务的错误统计
        return errorStatisticsCache.values.flatten().filter { stats ->
            (startTime == null || stats.lastOccurrence.after(startTime) || stats.lastOccurrence == startTime) &&
                (endTime == null || stats.firstOccurrence.before(endTime) || stats.firstOccurrence == endTime)
        }
    }

    override fun clearExecutionHistory(serviceId: String?, before: Date?): Int {
        val sizeBefore = executionRecords.size

        // 删除符合条件的记录
        executionRecords.removeIf { record ->
            (serviceId == null || record.serviceId == serviceId) &&
                (before == null || record.timestamp.before(before))
        }

        // 重新计算统计
        recalculateStatistics()

        return sizeBefore - executionRecords.size
    }

    /**
     * 更新服务统计
     */
    private fun updateServiceStatistics(record: ExecutionRecord) {
        val serviceId = record.serviceId

        // 获取现有统计或创建新统计
        val existingStats = serviceStatisticsCache[serviceId]

        if (existingStats == null) {
            // 创建新统计
            val newStats = ServiceStatistics(
                serviceId = serviceId,
                serviceName = record.serviceName,
                totalExecutions = 1,
                successfulExecutions = if (record.success) 1 else 0,
                failedExecutions = if (record.success) 0 else 1,
                averageExecutionTime = record.executionTime.toDouble(),
                maxExecutionTime = record.executionTime,
                minExecutionTime = record.executionTime,
                lastExecutionTime = record.timestamp
            )

            serviceStatisticsCache[serviceId] = newStats
        } else {
            // 更新现有统计
            val totalExecutions = existingStats.totalExecutions + 1
            val successfulExecutions = existingStats.successfulExecutions + (if (record.success) 1 else 0)
            val failedExecutions = existingStats.failedExecutions + (if (record.success) 0 else 1)

            // 计算新的平均执行时间
            val newAverageTime = (existingStats.averageExecutionTime * existingStats.totalExecutions + record.executionTime) / totalExecutions

            // 更新最大/最小执行时间
            val maxExecutionTime = maxOf(existingStats.maxExecutionTime, record.executionTime)
            val minExecutionTime = minOf(existingStats.minExecutionTime, record.executionTime)

            // 创建更新后的统计
            val updatedStats = existingStats.copy(
                totalExecutions = totalExecutions,
                successfulExecutions = successfulExecutions,
                failedExecutions = failedExecutions,
                averageExecutionTime = newAverageTime,
                maxExecutionTime = maxExecutionTime,
                minExecutionTime = minExecutionTime,
                lastExecutionTime = record.timestamp
            )

            serviceStatisticsCache[serviceId] = updatedStats
        }
    }

    /**
     * 更新错误统计
     */
    private fun updateErrorStatistics(record: ExecutionRecord) {
        val serviceId = record.serviceId
        val errorMessage = record.errorMessage ?: return

        // 获取服务的错误统计列表
        val serviceErrors = errorStatisticsCache.computeIfAbsent(serviceId) { mutableListOf() }

        // 查找现有错误统计
        val existingError = serviceErrors.find { it.errorMessage == errorMessage }

        if (existingError == null) {
            // 创建新的错误统计
            val newError = ErrorStatistics(
                serviceId = serviceId,
                serviceName = record.serviceName,
                errorMessage = errorMessage,
                occurrences = 1,
                firstOccurrence = record.timestamp,
                lastOccurrence = record.timestamp
            )

            serviceErrors.add(newError)
        } else {
            // 更新现有错误统计
            val updatedError = existingError.copy(
                occurrences = existingError.occurrences + 1,
                lastOccurrence = record.timestamp
            )

            // 替换现有错误统计
            val index = serviceErrors.indexOf(existingError)
            serviceErrors[index] = updatedError
        }
    }

    /**
     * 重新计算统计
     * 在清除历史记录后调用
     */
    private fun recalculateStatistics() {
        // 清除现有统计
        serviceStatisticsCache.clear()
        errorStatisticsCache.clear()

        // 重新计算服务统计
        for (record in executionRecords) {
            updateServiceStatistics(record)

            // 如果执行失败，更新错误统计
            if (!record.success && record.errorMessage != null) {
                updateErrorStatistics(record)
            }
        }
    }

    /**
     * 定期清理旧记录
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    fun cleanupOldRecords() {
        try {
            logger.info("开始清理旧执行记录")

            // 计算30天前的时间
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.DAY_OF_MONTH, -30)
            val thirtyDaysAgo = calendar.time

            // 清理30天前的记录
            val removedCount = clearExecutionHistory(null, thirtyDaysAgo)

            logger.info("清理完成，共删除 {} 条记录", removedCount)
        } catch (e: Exception) {
            logger.error("清理旧执行记录失败", e)
        }
    }
}
