package ai.magicdb.data.service.core.converter

import ai.magicdb.data.service.api.model.ServiceCallRecord
import ai.magicdb.data.service.api.model.ServiceCallStatistics
import ai.magicdb.data.service.api.model.ServiceErrorStatistics
import ai.magicdb.data.service.api.model.ServicePerformanceMetrics
import ai.magicdb.data.service.api.model.ServicePerformanceRecord
import ai.magicdb.data.service.core.entity.ServiceCallRecordDO
import ai.magicdb.data.service.core.entity.ServiceCallStatsDO
import ai.magicdb.data.service.core.entity.ServicePerformanceDO
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 监控数据转换器
 *
 * @author magicdb
 */
@Component
class MonitoringConverter(
    private val objectMapper: ObjectMapper
) {
    /**
     * 将服务调用记录转换为DO
     *
     * @param record 服务调用记录
     * @return 服务调用记录DO
     */
    fun toServiceCallRecordDO(record: ServiceCallRecord): ServiceCallRecordDO {
        return ServiceCallRecordDO(
            id = record.id,
            serviceId = record.serviceId,
            serviceName = record.serviceName ?: "",
            callTime = record.callTime,
            executionTime = record.executionTime,
            success = record.success,
            errorType = record.errorType,
            errorMessage = record.errorMessage,
            userId = record.userId,
            clientIp = record.clientIp,
            requestParams = record.parameters?.let { objectMapper.writeValueAsString(it) },
            responseResult = record.result?.let { objectMapper.writeValueAsString(it) },
            triggerType = record.triggerType ?: "MANUAL",
            triggerId = record.triggerId,
            metadata = record.metadata?.let { objectMapper.writeValueAsString(it) }
        )
    }
    
    /**
     * 将服务调用记录DO转换为服务调用记录
     *
     * @param recordDO 服务调用记录DO
     * @return 服务调用记录
     */
    fun toServiceCallRecord(recordDO: ServiceCallRecordDO): ServiceCallRecord {
        return ServiceCallRecord(
            id = recordDO.id,
            serviceId = recordDO.serviceId,
            serviceName = recordDO.serviceName,
            callTime = recordDO.callTime,
            executionTime = recordDO.executionTime,
            success = recordDO.success,
            errorType = recordDO.errorType,
            errorMessage = recordDO.errorMessage,
            userId = recordDO.userId,
            clientIp = recordDO.clientIp,
            parameters = recordDO.requestParams?.let { 
                objectMapper.readValue(it, object : TypeReference<Map<String, Any?>>() {})
            },
            result = recordDO.responseResult?.let { 
                objectMapper.readValue(it, Any::class.java)
            },
            triggerType = recordDO.triggerType,
            triggerId = recordDO.triggerId,
            metadata = recordDO.metadata?.let { 
                objectMapper.readValue(it, object : TypeReference<Map<String, Any>>() {})
            }
        )
    }
    
    /**
     * 将服务调用统计DO转换为服务调用统计
     *
     * @param statsDO 服务调用统计DO
     * @return 服务调用统计
     */
    fun toServiceCallStatistics(statsDO: ServiceCallStatsDO): ServiceCallStatistics {
        return ServiceCallStatistics(
            serviceId = statsDO.serviceId,
            serviceName = statsDO.serviceName,
            totalCalls = statsDO.totalCalls,
            successCalls = statsDO.successCalls,
            failedCalls = statsDO.failedCalls,
            successRate = statsDO.successRate,
            avgExecutionTime = statsDO.avgExecutionTime,
            maxExecutionTime = statsDO.maxExecutionTime,
            minExecutionTime = statsDO.minExecutionTime,
            uniqueUsers = statsDO.uniqueUsers,
            uniqueIps = statsDO.uniqueIps,
            errorTypes = statsDO.errorTypes?.let { 
                objectMapper.readValue(it, object : TypeReference<Map<String, Long>>() {})
            } ?: emptyMap(),
            mostCommonErrors = statsDO.commonErrors?.let { 
                objectMapper.readValue(it, object : TypeReference<List<Pair<String, Long>>>() {})
            } ?: emptyList(),
            statsTime = statsDO.statsTime,
            statsPeriod = statsDO.statsPeriod,
            lastCalledTime = null,
            startTime = null,
            endTime = null
        )
    }
    
    /**
     * 将服务调用统计转换为服务调用统计DO
     *
     * @param stats 服务调用统计
     * @return 服务调用统计DO
     */
    fun toServiceCallStatsDO(stats: ServiceCallStatistics): ServiceCallStatsDO {
        return ServiceCallStatsDO(
            id = "",
            serviceId = stats.serviceId,
            serviceName = stats.serviceName,
            statsTime = stats.statsTime ?: LocalDateTime.now(),
            totalCalls = stats.totalCalls,
            successCalls = stats.successCalls,
            failedCalls = stats.failedCalls,
            successRate = stats.successRate,
            avgExecutionTime = stats.avgExecutionTime,
            maxExecutionTime = stats.maxExecutionTime,
            minExecutionTime = stats.minExecutionTime,
            uniqueUsers = stats.uniqueUsers,
            uniqueIps = stats.uniqueIps,
            errorTypes = stats.errorTypes.takeIf { it.isNotEmpty() }?.let { 
                objectMapper.writeValueAsString(it) 
            },
            commonErrors = stats.mostCommonErrors.takeIf { it.isNotEmpty() }?.let { 
                objectMapper.writeValueAsString(it) 
            },
            statsPeriod = stats.statsPeriod
        )
    }
    
    /**
     * 将服务错误统计转换为服务调用统计DO
     *
     * @param errorStats 服务错误统计
     * @return 服务调用统计DO
     */
    fun toServiceCallStatsDO(errorStats: ServiceErrorStatistics): ServiceCallStatsDO {
        return ServiceCallStatsDO(
            id = "",
            serviceId = errorStats.serviceId,
            serviceName = errorStats.serviceName,
            statsTime = LocalDateTime.now(),
            totalCalls = errorStats.totalCalls,
            successCalls = errorStats.totalCalls - errorStats.totalErrors,
            failedCalls = errorStats.totalErrors,
            successRate = 1.0 - errorStats.errorRate,
            avgExecutionTime = 0.0,
            maxExecutionTime = 0,
            minExecutionTime = 0,
            uniqueUsers = 0,
            uniqueIps = 0,
            errorTypes = errorStats.errorTypes.takeIf { it.isNotEmpty() }?.let { 
                objectMapper.writeValueAsString(it) 
            },
            commonErrors = errorStats.mostCommonErrors.takeIf { it.isNotEmpty() }?.let { 
                objectMapper.writeValueAsString(it) 
            },
            statsPeriod = 0
        )
    }
    
    /**
     * 将服务性能指标转换为服务性能DO
     *
     * @param metrics 服务性能指标
     * @return 服务性能DO
     */
    fun toServicePerformanceDO(metrics: ServicePerformanceMetrics): ServicePerformanceDO {
        return ServicePerformanceDO(
            id = "",
            serviceId = metrics.serviceId,
            serviceName = metrics.serviceName,
            recordTime = LocalDateTime.now(),
            memoryUsage = metrics.memoryUsage,
            cpuUsage = metrics.cpuUsage,
            threadCount = 0,
            activeConnections = metrics.concurrentUsers,
            requestsPerSecond = metrics.requestsPerSecond,
            avgResponseTime = metrics.avgResponseTime,
            percentiles = metrics.percentiles.takeIf { it.isNotEmpty() }?.let { 
                objectMapper.writeValueAsString(it) 
            },
            responseTimeByTimeSlot = metrics.responseTimeByTimeSlot.takeIf { it.isNotEmpty() }?.let { 
                objectMapper.writeValueAsString(it) 
            },
            requestsByTimeSlot = metrics.requestsByTimeSlot.takeIf { it.isNotEmpty() }?.let { 
                objectMapper.writeValueAsString(it) 
            }
        )
    }
    
    /**
     * 将服务性能DO转换为服务性能指标
     *
     * @param performanceDO 服务性能DO
     * @return 服务性能指标
     */
    fun toServicePerformanceMetrics(performanceDO: ServicePerformanceDO): ServicePerformanceMetrics {
        return ServicePerformanceMetrics(
            serviceId = performanceDO.serviceId,
            serviceName = performanceDO.serviceName,
            avgResponseTime = performanceDO.avgResponseTime,
            maxResponseTime = 0,
            minResponseTime = 0,
            percentiles = performanceDO.percentiles?.let { 
                objectMapper.readValue(it, object : TypeReference<Map<String, Double>>() {})
            } ?: emptyMap(),
            requestsPerSecond = performanceDO.requestsPerSecond,
            concurrentUsers = performanceDO.activeConnections,
            memoryUsage = performanceDO.memoryUsage,
            cpuUsage = performanceDO.cpuUsage,
            startTime = performanceDO.recordTime.minusHours(1),
            endTime = performanceDO.recordTime,
            responseTimeByTimeSlot = performanceDO.responseTimeByTimeSlot?.let { 
                objectMapper.readValue(it, object : TypeReference<Map<String, Double>>() {})
            } ?: emptyMap(),
            requestsByTimeSlot = performanceDO.requestsByTimeSlot?.let { 
                objectMapper.readValue(it, object : TypeReference<Map<String, Int>>() {})
            } ?: emptyMap()
        )
    }
    
    /**
     * 将服务性能记录转换为服务性能DO
     *
     * @param record 服务性能记录
     * @return 服务性能DO
     */
    fun toServicePerformanceDO(record: ServicePerformanceRecord): ServicePerformanceDO {
        return ServicePerformanceDO(
            id = record.id,
            serviceId = record.serviceId,
            serviceName = record.serviceName,
            recordTime = record.recordTime,
            memoryUsage = record.memoryUsage,
            cpuUsage = record.cpuUsage,
            threadCount = record.threadCount,
            activeConnections = record.activeConnections,
            requestsPerSecond = record.requestsPerSecond,
            avgResponseTime = record.avgResponseTime,
            metadata = record.metadata?.let { objectMapper.writeValueAsString(it) }
        )
    }
    
    /**
     * 将服务性能DO转换为服务性能记录
     *
     * @param performanceDO 服务性能DO
     * @return 服务性能记录
     */
    fun toServicePerformanceRecord(performanceDO: ServicePerformanceDO): ServicePerformanceRecord {
        return ServicePerformanceRecord(
            id = performanceDO.id,
            serviceId = performanceDO.serviceId,
            serviceName = performanceDO.serviceName,
            recordTime = performanceDO.recordTime,
            memoryUsage = performanceDO.memoryUsage,
            cpuUsage = performanceDO.cpuUsage,
            threadCount = performanceDO.threadCount,
            activeConnections = performanceDO.activeConnections,
            requestsPerSecond = performanceDO.requestsPerSecond,
            avgResponseTime = performanceDO.avgResponseTime,
            metadata = performanceDO.metadata?.let { 
                objectMapper.readValue(it, object : TypeReference<Map<String, Any>>() {})
            }
        )
    }
}
