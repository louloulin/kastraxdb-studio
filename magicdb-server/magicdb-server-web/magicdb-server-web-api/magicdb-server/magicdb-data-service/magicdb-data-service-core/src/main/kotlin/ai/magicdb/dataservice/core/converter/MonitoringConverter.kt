package ai.magicdb.dataservice.core.converter

import ai.magicdb.dataservice.api.model.ServiceCallRecord
import ai.magicdb.dataservice.api.model.ServiceCallStatistics
import ai.magicdb.dataservice.api.model.ServiceErrorStatistics
import ai.magicdb.dataservice.api.model.ServicePerformanceMetrics
import ai.magicdb.dataservice.core.entity.ServiceCallRecordDO
import ai.magicdb.dataservice.core.entity.ServiceCallStatsDO
import ai.magicdb.dataservice.core.entity.ServicePerformanceDO
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

/**
 * 监控数据转换器
 *
 * @author magicdb
 */
@Component
class MonitoringConverter(private val objectMapper: ObjectMapper) {
    
    /**
     * 将调用记录模型转换为DO
     *
     * @param record 调用记录模型
     * @return 调用记录DO
     */
    fun toDO(record: ServiceCallRecord): ServiceCallRecordDO {
        return ServiceCallRecordDO(
            id = record.id,
            serviceId = record.serviceId,
            serviceName = record.serviceName,
            callTime = record.callTime,
            executionTime = record.executionTime,
            success = record.success,
            errorMessage = record.errorMessage,
            errorType = record.errorType,
            userId = record.userId,
            clientIp = record.clientIp,
            requestParams = record.requestParams?.let { objectMapper.writeValueAsString(it) },
            responseData = record.responseData?.let { objectMapper.writeValueAsString(it) },
            method = record.method,
            path = record.path,
            source = record.source
        )
    }
    
    /**
     * 将调用记录DO转换为模型
     *
     * @param recordDO 调用记录DO
     * @return 调用记录模型
     */
    fun toModel(recordDO: ServiceCallRecordDO): ServiceCallRecord {
        return ServiceCallRecord(
            id = recordDO.id,
            serviceId = recordDO.serviceId,
            serviceName = recordDO.serviceName ?: "",
            callTime = recordDO.callTime,
            executionTime = recordDO.executionTime,
            success = recordDO.success,
            errorMessage = recordDO.errorMessage,
            errorType = recordDO.errorType,
            userId = recordDO.userId,
            clientIp = recordDO.clientIp,
            requestParams = recordDO.requestParams?.let {
                objectMapper.readValue(it, object : TypeReference<Map<String, Any?>>() {})
            },
            responseData = recordDO.responseData?.let { objectMapper.readValue(it, Any::class.java) },
            method = recordDO.method,
            path = recordDO.path,
            source = recordDO.source
        )
    }
    
    /**
     * 将调用统计模型转换为DO
     *
     * @param stats 调用统计模型
     * @param statType 统计类型
     * @param statPeriod 统计周期
     * @return 调用统计DO
     */
    fun toDO(stats: ServiceCallStatistics, statType: String, statPeriod: String): ServiceCallStatsDO {
        return ServiceCallStatsDO(
            id = UUID.randomUUID().toString(),
            serviceId = stats.serviceId,
            serviceName = stats.serviceName,
            statType = statType,
            statPeriod = statPeriod,
            startTime = stats.startTime,
            endTime = stats.endTime,
            totalCalls = stats.totalCalls,
            successCalls = stats.successCalls,
            failedCalls = stats.failedCalls,
            avgExecutionTime = stats.avgExecutionTime,
            maxExecutionTime = stats.maxExecutionTime,
            minExecutionTime = stats.minExecutionTime,
            uniqueUsers = stats.uniqueUsers,
            uniqueIps = stats.uniqueIps,
            errorTypes = null, // 不存储错误类型统计
            commonErrors = null, // 不存储常见错误
            callsByHour = objectMapper.writeValueAsString(stats.callsByHour),
            callsByDay = objectMapper.writeValueAsString(stats.callsByDay),
            updateTime = LocalDateTime.now()
        )
    }
    
    /**
     * 将调用统计DO转换为模型
     *
     * @param statsDO 调用统计DO
     * @return 调用统计模型
     */
    fun toCallStatisticsModel(statsDO: ServiceCallStatsDO): ServiceCallStatistics {
        return ServiceCallStatistics(
            serviceId = statsDO.serviceId,
            serviceName = statsDO.serviceName ?: "",
            totalCalls = statsDO.totalCalls,
            successCalls = statsDO.successCalls,
            failedCalls = statsDO.failedCalls,
            successRate = if (statsDO.totalCalls > 0) statsDO.successCalls.toDouble() / statsDO.totalCalls else 0.0,
            avgExecutionTime = statsDO.avgExecutionTime,
            maxExecutionTime = statsDO.maxExecutionTime,
            minExecutionTime = statsDO.minExecutionTime,
            lastCalledTime = statsDO.endTime, // 使用结束时间作为最后调用时间
            startTime = statsDO.startTime,
            endTime = statsDO.endTime,
            uniqueUsers = statsDO.uniqueUsers,
            uniqueIps = statsDO.uniqueIps,
            callsByHour = statsDO.callsByHour?.let {
                objectMapper.readValue(it, object : TypeReference<Map<Int, Long>>() {})
            } ?: emptyMap(),
            callsByDay = statsDO.callsByDay?.let {
                objectMapper.readValue(it, object : TypeReference<Map<String, Long>>() {})
            } ?: emptyMap()
        )
    }
    
    /**
     * 将性能指标模型转换为DO
     *
     * @param metrics 性能指标模型
     * @return 性能指标DO
     */
    fun toDO(metrics: ServicePerformanceMetrics): ServicePerformanceDO {
        val metadata = mutableMapOf<String, Any>()
        metadata["percentiles"] = metrics.percentiles
        metadata["responseTimeByTimeSlot"] = metrics.responseTimeByTimeSlot
        metadata["requestsByTimeSlot"] = metrics.requestsByTimeSlot
        
        return ServicePerformanceDO(
            id = UUID.randomUUID().toString(),
            serviceId = metrics.serviceId,
            serviceName = metrics.serviceName,
            recordTime = LocalDateTime.now(),
            memoryUsage = metrics.memoryUsage,
            cpuUsage = metrics.cpuUsage,
            threadCount = 0, // 暂不实现
            activeConnections = 0, // 暂不实现
            requestsPerSecond = metrics.requestsPerSecond,
            avgResponseTime = metrics.avgResponseTime,
            metadata = objectMapper.writeValueAsString(metadata)
        )
    }
    
    /**
     * 将性能指标DO转换为模型
     *
     * @param performanceDO 性能指标DO
     * @return 性能指标模型
     */
    fun toPerformanceMetricsModel(performanceDO: ServicePerformanceDO): ServicePerformanceMetrics {
        val metadata = performanceDO.metadata?.let {
            objectMapper.readValue(it, object : TypeReference<Map<String, Any>>() {})
        } ?: emptyMap<String, Any>()
        
        @Suppress("UNCHECKED_CAST")
        val percentiles = metadata["percentiles"] as? Map<String, Long> ?: emptyMap()
        
        @Suppress("UNCHECKED_CAST")
        val responseTimeByTimeSlot = metadata["responseTimeByTimeSlot"] as? Map<String, Double> ?: emptyMap()
        
        @Suppress("UNCHECKED_CAST")
        val requestsByTimeSlot = metadata["requestsByTimeSlot"] as? Map<String, Long> ?: emptyMap()
        
        return ServicePerformanceMetrics(
            serviceId = performanceDO.serviceId ?: "",
            serviceName = performanceDO.serviceName ?: "",
            avgResponseTime = performanceDO.avgResponseTime,
            maxResponseTime = 0, // 从DO中无法获取
            minResponseTime = 0, // 从DO中无法获取
            percentiles = percentiles,
            requestsPerSecond = performanceDO.requestsPerSecond,
            concurrentUsers = 0, // 从DO中无法获取
            memoryUsage = performanceDO.memoryUsage,
            cpuUsage = performanceDO.cpuUsage,
            startTime = LocalDateTime.MIN, // 从DO中无法获取
            endTime = performanceDO.recordTime,
            responseTimeByTimeSlot = responseTimeByTimeSlot,
            requestsByTimeSlot = requestsByTimeSlot
        )
    }
}
