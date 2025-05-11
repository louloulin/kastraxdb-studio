package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.MonitoringRepository
import ai.magicdb.dataservice.api.model.ServiceCallRecord
import ai.magicdb.dataservice.api.model.ServicePerformanceRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 内存监控存储库实现
 *
 * @author magicdb
 */
@Repository
class MemoryMonitoringRepository : MonitoringRepository {
    private val logger = LoggerFactory.getLogger(MemoryMonitoringRepository::class.java)
    
    // 调用记录存储
    private val callRecords = ConcurrentHashMap<String, ServiceCallRecord>()
    
    // 性能记录存储
    private val performanceRecords = ConcurrentHashMap<String, ServicePerformanceRecord>()
    
    // 日期格式化器
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    override fun saveCallRecord(record: ServiceCallRecord): String {
        try {
            // 生成ID
            val id = record.id.ifEmpty { UUID.randomUUID().toString() }
            
            // 创建新记录
            val newRecord = if (record.id.isEmpty()) {
                record.copy(id = id)
            } else {
                record
            }
            
            // 保存记录
            callRecords[id] = newRecord
            
            return id
        } catch (e: Exception) {
            logger.error("保存调用记录失败: {}", e.message, e)
            throw e
        }
    }
    
    override fun getCallRecord(recordId: String): ServiceCallRecord? {
        try {
            return callRecords[recordId]
        } catch (e: Exception) {
            logger.error("获取调用记录失败: {}", recordId, e)
            return null
        }
    }
    
    override fun getCallRecords(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        success: Boolean?,
        limit: Int,
        offset: Int
    ): List<ServiceCallRecord> {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                (success == null || record.success == success)
            }
            
            // 排序记录（按调用时间降序）
            val sortedRecords = filteredRecords.sortedByDescending { it.callTime }
            
            // 分页
            return sortedRecords.drop(offset).take(limit)
        } catch (e: Exception) {
            logger.error("获取调用记录列表失败", e)
            return emptyList()
        }
    }
    
    override fun getCallRecordCount(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        success: Boolean?
    ): Long {
        try {
            // 过滤并计数
            return callRecords.values.count { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                (success == null || record.success == success)
            }.toLong()
        } catch (e: Exception) {
            logger.error("获取调用记录数量失败", e)
            return 0
        }
    }
    
    override fun getSuccessCallCount(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Long {
        return getCallRecordCount(serviceId, startTime, endTime, true)
    }
    
    override fun getFailedCallCount(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Long {
        return getCallRecordCount(serviceId, startTime, endTime, false)
    }
    
    override fun getAvgExecutionTime(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        success: Boolean?
    ): Double {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                (success == null || record.success == success)
            }
            
            // 计算平均执行时间
            return if (filteredRecords.isNotEmpty()) {
                filteredRecords.map { it.executionTime }.average()
            } else {
                0.0
            }
        } catch (e: Exception) {
            logger.error("获取平均执行时间失败", e)
            return 0.0
        }
    }
    
    override fun getMaxExecutionTime(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        success: Boolean?
    ): Long {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                (success == null || record.success == success)
            }
            
            // 计算最大执行时间
            return filteredRecords.maxOfOrNull { it.executionTime } ?: 0
        } catch (e: Exception) {
            logger.error("获取最大执行时间失败", e)
            return 0
        }
    }
    
    override fun getMinExecutionTime(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        success: Boolean?
    ): Long {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                (success == null || record.success == success)
            }
            
            // 计算最小执行时间
            return filteredRecords.minOfOrNull { it.executionTime } ?: 0
        } catch (e: Exception) {
            logger.error("获取最小执行时间失败", e)
            return 0
        }
    }
    
    override fun getLastCallTime(serviceId: String?): LocalDateTime? {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                serviceId == null || record.serviceId == serviceId
            }
            
            // 获取最后调用时间
            return filteredRecords.maxByOrNull { it.callTime }?.callTime
        } catch (e: Exception) {
            logger.error("获取最后调用时间失败", e)
            return null
        }
    }
    
    override fun getLastErrorTime(serviceId: String): LocalDateTime? {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                record.serviceId == serviceId && !record.success
            }
            
            // 获取最后错误时间
            return filteredRecords.maxByOrNull { it.callTime }?.callTime
        } catch (e: Exception) {
            logger.error("获取最后错误时间失败", e)
            return null
        }
    }
    
    override fun getUniqueUserCount(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Int {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                record.userId != null
            }
            
            // 计算唯一用户数
            return filteredRecords.mapNotNull { it.userId }.toSet().size
        } catch (e: Exception) {
            logger.error("获取唯一用户数失败", e)
            return 0
        }
    }
    
    override fun getUniqueIpCount(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Int {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                !record.clientIp.isNullOrEmpty()
            }
            
            // 计算唯一IP数
            return filteredRecords.mapNotNull { it.clientIp }.toSet().size
        } catch (e: Exception) {
            logger.error("获取唯一IP数失败", e)
            return 0
        }
    }
    
    override fun getErrorTypeStats(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Map<String, Long> {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                !record.success &&
                !record.errorType.isNullOrEmpty()
            }
            
            // 统计错误类型
            return filteredRecords
                .groupBy { it.errorType ?: "Unknown" }
                .mapValues { it.value.size.toLong() }
        } catch (e: Exception) {
            logger.error("获取错误类型统计失败", e)
            return emptyMap()
        }
    }
    
    override fun getMostCommonErrors(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        limit: Int
    ): List<Pair<String, Long>> {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                !record.success &&
                !record.errorMessage.isNullOrEmpty()
            }
            
            // 统计错误消息
            return filteredRecords
                .groupBy { it.errorMessage ?: "Unknown" }
                .mapValues { it.value.size.toLong() }
                .entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { it.key to it.value }
        } catch (e: Exception) {
            logger.error("获取最常见错误失败", e)
            return emptyList()
        }
    }
    
    override fun getRecentlyCalledServices(limit: Int): List<String> {
        try {
            // 按服务ID分组
            val serviceGroups = callRecords.values.groupBy { it.serviceId }
            
            // 获取每个服务的最后调用时间
            val serviceLastCallTimes = serviceGroups.mapValues { (_, records) ->
                records.maxByOrNull { it.callTime }?.callTime ?: LocalDateTime.MIN
            }
            
            // 按最后调用时间排序并获取前N个
            return serviceLastCallTimes.entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { it.key }
        } catch (e: Exception) {
            logger.error("获取最近调用服务失败", e)
            return emptyList()
        }
    }
    
    override fun getMostCalledServices(
        limit: Int,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<Pair<String, Long>> {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime))
            }
            
            // 按服务ID分组并计数
            val serviceCounts = filteredRecords
                .groupBy { it.serviceId }
                .mapValues { it.value.size.toLong() }
            
            // 按调用次数排序并获取前N个
            return serviceCounts.entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { it.key to it.value }
        } catch (e: Exception) {
            logger.error("获取调用最多服务失败", e)
            return emptyList()
        }
    }
    
    override fun getMostErrorServices(
        limit: Int,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<Pair<String, Long>> {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                !record.success
            }
            
            // 按服务ID分组并计数
            val serviceErrorCounts = filteredRecords
                .groupBy { it.serviceId }
                .mapValues { it.value.size.toLong() }
            
            // 按错误次数排序并获取前N个
            return serviceErrorCounts.entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { it.key to it.value }
        } catch (e: Exception) {
            logger.error("获取错误最多服务失败", e)
            return emptyList()
        }
    }
    
    override fun getWorstPerformanceServices(
        limit: Int,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<Pair<String, Double>> {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                record.success // 只考虑成功的调用
            }
            
            // 按服务ID分组并计算平均执行时间
            val serviceAvgTimes = filteredRecords
                .groupBy { it.serviceId }
                .mapValues { (_, records) -> records.map { it.executionTime }.average() }
                .filter { it.value > 0 } // 过滤掉没有数据的服务
            
            // 按平均执行时间排序并获取前N个
            return serviceAvgTimes.entries
                .sortedByDescending { it.value }
                .take(limit)
                .map { it.key to it.value }
        } catch (e: Exception) {
            logger.error("获取性能最差服务失败", e)
            return emptyList()
        }
    }
    
    override fun getCallsByHour(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Map<Int, Long> {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime))
            }
            
            // 按小时分组并计数
            return filteredRecords
                .groupBy { it.callTime.hour }
                .mapValues { it.value.size.toLong() }
        } catch (e: Exception) {
            logger.error("获取按小时统计的调用次数失败", e)
            return emptyMap()
        }
    }
    
    override fun getCallsByDay(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Map<String, Long> {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime))
            }
            
            // 按天分组并计数
            return filteredRecords
                .groupBy { it.callTime.format(dateFormatter) }
                .mapValues { it.value.size.toLong() }
        } catch (e: Exception) {
            logger.error("获取按天统计的调用次数失败", e)
            return emptyMap()
        }
    }
    
    override fun getErrorsByHour(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Map<Int, Long> {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                !record.success
            }
            
            // 按小时分组并计数
            return filteredRecords
                .groupBy { it.callTime.hour }
                .mapValues { it.value.size.toLong() }
        } catch (e: Exception) {
            logger.error("获取按小时统计的错误次数失败", e)
            return emptyMap()
        }
    }
    
    override fun getErrorsByDay(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Map<String, Long> {
        try {
            // 过滤记录
            val filteredRecords = callRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.callTime.isBefore(startTime)) &&
                (endTime == null || !record.callTime.isAfter(endTime)) &&
                !record.success
            }
            
            // 按天分组并计数
            return filteredRecords
                .groupBy { it.callTime.format(dateFormatter) }
                .mapValues { it.value.size.toLong() }
        } catch (e: Exception) {
            logger.error("获取按天统计的错误次数失败", e)
            return emptyMap()
        }
    }
    
    override fun clearMonitoringData(
        serviceId: String?,
        before: LocalDateTime?
    ): Int {
        try {
            // 获取要删除的记录ID
            val recordsToRemove = callRecords.values
                .filter { record ->
                    (serviceId == null || record.serviceId == serviceId) &&
                    (before == null || record.callTime.isBefore(before))
                }
                .map { it.id }
            
            // 删除记录
            recordsToRemove.forEach { callRecords.remove(it) }
            
            return recordsToRemove.size
        } catch (e: Exception) {
            logger.error("清除监控数据失败", e)
            return 0
        }
    }
    
    override fun savePerformanceRecord(record: ServicePerformanceRecord): String {
        try {
            // 生成ID
            val id = record.id.ifEmpty { UUID.randomUUID().toString() }
            
            // 创建新记录
            val newRecord = if (record.id.isEmpty()) {
                record.copy(id = id)
            } else {
                record
            }
            
            // 保存记录
            performanceRecords[id] = newRecord
            
            return id
        } catch (e: Exception) {
            logger.error("保存性能记录失败: {}", e.message, e)
            throw e
        }
    }
    
    override fun getPerformanceRecord(recordId: String): ServicePerformanceRecord? {
        try {
            return performanceRecords[recordId]
        } catch (e: Exception) {
            logger.error("获取性能记录失败: {}", recordId, e)
            return null
        }
    }
    
    override fun getPerformanceRecords(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        limit: Int,
        offset: Int
    ): List<ServicePerformanceRecord> {
        try {
            // 过滤记录
            val filteredRecords = performanceRecords.values.filter { record ->
                (serviceId == null || record.serviceId == serviceId) &&
                (startTime == null || !record.recordTime.isBefore(startTime)) &&
                (endTime == null || !record.recordTime.isAfter(endTime))
            }
            
            // 排序记录（按记录时间降序）
            val sortedRecords = filteredRecords.sortedByDescending { it.recordTime }
            
            // 分页
            return sortedRecords.drop(offset).take(limit)
        } catch (e: Exception) {
            logger.error("获取性能记录列表失败", e)
            return emptyList()
        }
    }
}
