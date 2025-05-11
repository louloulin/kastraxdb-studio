package ai.magicdb.dataservice.core.repository

import ai.magicdb.dataservice.api.MonitoringRepository
import ai.magicdb.dataservice.api.model.ServiceCallRecord
import ai.magicdb.dataservice.core.converter.MonitoringConverter
import ai.magicdb.dataservice.core.mapper.ServiceCallRecordMapper
import ai.magicdb.dataservice.core.mapper.ServiceCallStatsMapper
import ai.magicdb.dataservice.core.mapper.ServicePerformanceMapper
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 基于MyBatis的监控存储库实现
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
    
    // 日期格式化器
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    override fun saveCallRecord(record: ServiceCallRecord): String {
        try {
            // 转换为DO对象
            val recordDO = monitoringConverter.toDO(record)
            
            // 保存记录
            callRecordMapper.insert(recordDO)
            
            return recordDO.id
        } catch (e: Exception) {
            logger.error("保存调用记录失败: {}", e.message, e)
            throw e
        }
    }
    
    override fun getCallRecord(recordId: String): ServiceCallRecord? {
        try {
            // 查询记录
            val recordDO = callRecordMapper.selectById(recordId) ?: return null
            
            // 转换为模型对象
            return monitoringConverter.toModel(recordDO)
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
            val wrapper = Wrappers.lambdaQuery<ai.magicdb.dataservice.core.entity.ServiceCallRecordDO>()
            
            // 添加查询条件
            if (serviceId != null) {
                wrapper.eq(ai.magicdb.dataservice.core.entity.ServiceCallRecordDO::serviceId, serviceId)
            }
            if (startTime != null) {
                wrapper.ge(ai.magicdb.dataservice.core.entity.ServiceCallRecordDO::callTime, startTime)
            }
            if (endTime != null) {
                wrapper.le(ai.magicdb.dataservice.core.entity.ServiceCallRecordDO::callTime, endTime)
            }
            if (success != null) {
                wrapper.eq(ai.magicdb.dataservice.core.entity.ServiceCallRecordDO::success, success)
            }
            
            // 按调用时间降序排序
            wrapper.orderByDesc(ai.magicdb.dataservice.core.entity.ServiceCallRecordDO::callTime)
            
            // 分页查询
            val page = Page<ai.magicdb.dataservice.core.entity.ServiceCallRecordDO>(
                (offset / limit + 1).toLong(),
                limit.toLong()
            )
            val result = callRecordMapper.selectPage(page, wrapper)
            
            // 转换为模型对象
            return result.records.map { monitoringConverter.toModel(it) }
        } catch (e: Exception) {
            logger.error("获取调用记录列表失败", e)
            return emptyList()
        }
    }
    
    override fun getCallRecordCount(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Long {
        try {
            return callRecordMapper.countRecords(serviceId, startTime, endTime, null)
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
        try {
            return callRecordMapper.countRecords(serviceId, startTime, endTime, true)
        } catch (e: Exception) {
            logger.error("获取成功调用次数失败", e)
            return 0
        }
    }
    
    override fun getFailedCallCount(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Long {
        try {
            return callRecordMapper.countRecords(serviceId, startTime, endTime, false)
        } catch (e: Exception) {
            logger.error("获取失败调用次数失败", e)
            return 0
        }
    }
    
    override fun getAvgExecutionTime(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        success: Boolean?
    ): Double {
        try {
            return callRecordMapper.avgExecutionTime(serviceId, startTime, endTime, success) ?: 0.0
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
            return callRecordMapper.maxExecutionTime(serviceId, startTime, endTime, success) ?: 0
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
            return callRecordMapper.minExecutionTime(serviceId, startTime, endTime, success) ?: 0
        } catch (e: Exception) {
            logger.error("获取最小执行时间失败", e)
            return 0
        }
    }
    
    override fun getLastCallTime(serviceId: String): LocalDateTime? {
        try {
            return callRecordMapper.lastCallTime(serviceId)
        } catch (e: Exception) {
            logger.error("获取最后调用时间失败: {}", serviceId, e)
            return null
        }
    }
    
    override fun getLastErrorTime(serviceId: String): LocalDateTime? {
        try {
            return callRecordMapper.lastErrorTime(serviceId)
        } catch (e: Exception) {
            logger.error("获取最后错误时间失败: {}", serviceId, e)
            return null
        }
    }
    
    override fun getUniqueUserCount(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Int {
        try {
            return callRecordMapper.uniqueUserCount(serviceId, startTime, endTime)
        } catch (e: Exception) {
            logger.error("获取调用用户数失败", e)
            return 0
        }
    }
    
    override fun getUniqueIpCount(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Int {
        try {
            return callRecordMapper.uniqueIpCount(serviceId, startTime, endTime)
        } catch (e: Exception) {
            logger.error("获取调用IP数失败", e)
            return 0
        }
    }
    
    override fun getErrorTypeStats(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Map<String, Long> {
        try {
            val result = callRecordMapper.errorTypeStats(serviceId, startTime, endTime)
            return result.associate { 
                it["error_type"].toString() to (it["error_count"] as Number).toLong() 
            }
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
            val result = callRecordMapper.mostCommonErrors(serviceId, startTime, endTime, limit)
            return result.map { 
                it["error_message"].toString() to (it["error_count"] as Number).toLong() 
            }
        } catch (e: Exception) {
            logger.error("获取最常见错误失败", e)
            return emptyList()
        }
    }
    
    override fun getRecentlyCalledServices(limit: Int): List<String> {
        try {
            return callRecordMapper.recentlyCalledServices(limit)
        } catch (e: Exception) {
            logger.error("获取最近调用的服务失败", e)
            return emptyList()
        }
    }
    
    override fun getMostCalledServices(
        limit: Int,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<Pair<String, Long>> {
        try {
            val result = callRecordMapper.mostCalledServices(limit, startTime, endTime)
            return result.map { 
                it["service_id"].toString() to (it["call_count"] as Number).toLong() 
            }
        } catch (e: Exception) {
            logger.error("获取调用最多的服务失败", e)
            return emptyList()
        }
    }
    
    override fun getMostErrorServices(
        limit: Int,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<Pair<String, Long>> {
        try {
            val result = callRecordMapper.mostErrorServices(limit, startTime, endTime)
            return result.map { 
                it["service_id"].toString() to (it["error_count"] as Number).toLong() 
            }
        } catch (e: Exception) {
            logger.error("获取错误最多的服务失败", e)
            return emptyList()
        }
    }
    
    override fun getWorstPerformanceServices(
        limit: Int,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): List<Pair<String, Double>> {
        try {
            val result = callRecordMapper.worstPerformanceServices(limit, startTime, endTime)
            return result.map { 
                it["service_id"].toString() to (it["avg_time"] as Number).toDouble() 
            }
        } catch (e: Exception) {
            logger.error("获取性能最差的服务失败", e)
            return emptyList()
        }
    }
    
    override fun getCallsByHour(
        serviceId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?
    ): Map<Int, Long> {
        try {
            val result = callRecordMapper.callsByHour(serviceId, startTime, endTime)
            return result.associate { 
                (it["hour"] as Number).toInt() to (it["call_count"] as Number).toLong() 
            }
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
            val result = callRecordMapper.callsByDay(serviceId, startTime, endTime)
            return result.associate { 
                it["day"].toString() to (it["call_count"] as Number).toLong() 
            }
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
            val result = callRecordMapper.errorsByHour(serviceId, startTime, endTime)
            return result.associate { 
                (it["hour"] as Number).toInt() to (it["error_count"] as Number).toLong() 
            }
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
            val result = callRecordMapper.errorsByDay(serviceId, startTime, endTime)
            return result.associate { 
                it["day"].toString() to (it["error_count"] as Number).toLong() 
            }
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
}
