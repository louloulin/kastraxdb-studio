package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.MonitoringRepository
import ai.magicdb.dataservice.api.model.ServiceCallRecord
import ai.magicdb.dataservice.api.model.ServicePerformanceRecord
import ai.magicdb.dataservice.core.converter.MonitoringConverter
import ai.magicdb.dataservice.core.mapper.ServiceCallRecordMapper
import ai.magicdb.dataservice.core.mapper.ServiceCallStatsMapper
import ai.magicdb.dataservice.core.mapper.ServicePerformanceMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

/**
 * MyBatis监控存储库实现
 *
 * @author magicdb
 */
@Repository
class MybatisMonitoringRepository(
    private val callRecordMapper: ServiceCallRecordMapper,
    private val callStatsMapper: ServiceCallStatsMapper,
    private val performanceMapper: ServicePerformanceMapper,
    private val monitoringConverter: MonitoringConverter
) : MonitoringRepository {
    private val logger = LoggerFactory.getLogger(MybatisMonitoringRepository::class.java)
    
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
            
            // 转换为DO
            val recordDO = monitoringConverter.toServiceCallRecordDO(newRecord)
            
            // 保存记录
            callRecordMapper.insert(recordDO)
            
            return id
        } catch (e: Exception) {
            logger.error("保存调用记录失败: {}", e.message, e)
            throw e
        }
    }
    
    override fun getCallRecord(recordId: String): ServiceCallRecord? {
        try {
            // 查询记录
            val recordDO = callRecordMapper.selectById(recordId) ?: return null
            
            // 转换为API模型
            return monitoringConverter.toServiceCallRecord(recordDO)
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
            // 构建查询条件
            val wrapper = com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ai.magicdb.dataservice.core.entity.ServiceCallRecordDO>()
            
            // 添加条件
            if (serviceId != null) {
                wrapper.eq("service_id", serviceId)
            }
            if (startTime != null) {
                wrapper.ge("call_time", startTime)
            }
            if (endTime != null) {
                wrapper.le("call_time", endTime)
            }
            if (success != null) {
                wrapper.eq("success", success)
            }
            
            // 排序
            wrapper.orderByDesc("call_time")
            
            // 分页
            wrapper.last("LIMIT $limit OFFSET $offset")
            
            // 查询记录
            val records = callRecordMapper.selectList(wrapper)
            
            // 转换为API模型
            return records.map { monitoringConverter.toServiceCallRecord(it) }
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
            return callRecordMapper.getCallRecordCount(serviceId, startTime, endTime, success)
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
            return callRecordMapper.getAvgExecutionTime(serviceId, startTime, endTime, success)
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
            return callRecordMapper.getMaxExecutionTime(serviceId, startTime, endTime, success)
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
            return callRecordMapper.getMinExecutionTime(serviceId, startTime, endTime, success)
        } catch (e: Exception) {
            logger.error("获取最小执行时间失败", e)
            return 0
        }
    }
    
    override fun getLastCallTime(serviceId: String?): LocalDateTime? {
        try {
            return callRecordMapper.getLastCallTime(serviceId)
        } catch (e: Exception) {
            logger.error("获取最后调用时间失败", e)
            return null
        }
    }
    
    override fun getLastErrorTime(serviceId: String): LocalDateTime? {
        try {
            return callRecordMapper.getLastErrorTime(serviceId)
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
            return callRecordMapper.getUniqueUserCount(serviceId, startTime, endTime)
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
            return callRecordMapper.getUniqueIpCount(serviceId, startTime, endTime)
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
            return callRecordMapper.getErrorTypeStats(serviceId, startTime, endTime)
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
            val result = callRecordMapper.getMostCommonErrors(serviceId, startTime, endTime, limit)
            return result.map { it["error_message"].toString() to (it["count"] as Number).toLong() }
        } catch (e: Exception) {
            logger.error("获取最常见错误失败", e)
            return emptyList()
        }
    }
    
    override fun getRecentlyCalledServices(limit: Int): List<String> {
        try {
            return callRecordMapper.getRecentlyCalledServices(limit)
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
            val result = callRecordMapper.getMostCalledServices(limit, startTime, endTime)
            return result.map { it["service_id"].toString() to (it["count"] as Number).toLong() }
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
            val result = callRecordMapper.getMostErrorServices(limit, startTime, endTime)
            return result.map { it["service_id"].toString() to (it["count"] as Number).toLong() }
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
            val result = callRecordMapper.getWorstPerformanceServices(limit, startTime, endTime)
            return result.map { it["service_id"].toString() to (it["avg_time"] as Number).toDouble() }
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
            val result = callRecordMapper.getCallsByHour(serviceId, startTime, endTime)
            return result.associate { (it["hour"] as Number).toInt() to (it["count"] as Number).toLong() }
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
            val result = callRecordMapper.getCallsByDay(serviceId, startTime, endTime)
            return result.associate { it["day"].toString() to (it["count"] as Number).toLong() }
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
            val result = callRecordMapper.getErrorsByHour(serviceId, startTime, endTime)
            return result.associate { (it["hour"] as Number).toInt() to (it["count"] as Number).toLong() }
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
            val result = callRecordMapper.getErrorsByDay(serviceId, startTime, endTime)
            return result.associate { it["day"].toString() to (it["count"] as Number).toLong() }
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
            return callRecordMapper.clearMonitoringData(serviceId, before)
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
            
            // 转换为DO
            val recordDO = monitoringConverter.toServicePerformanceDO(newRecord)
            
            // 保存记录
            performanceMapper.insert(recordDO)
            
            return id
        } catch (e: Exception) {
            logger.error("保存性能记录失败: {}", e.message, e)
            throw e
        }
    }
    
    override fun getPerformanceRecord(recordId: String): ServicePerformanceRecord? {
        try {
            // 查询记录
            val recordDO = performanceMapper.selectById(recordId) ?: return null
            
            // 转换为API模型
            return monitoringConverter.toServicePerformanceRecord(recordDO)
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
            // 查询记录
            val records = performanceMapper.getServicePerformanceRecords(
                serviceId, startTime, endTime, limit, offset
            )
            
            // 转换为API模型
            return records.map { monitoringConverter.toServicePerformanceRecord(it) }
        } catch (e: Exception) {
            logger.error("获取性能记录列表失败", e)
            return emptyList()
        }
    }
}
