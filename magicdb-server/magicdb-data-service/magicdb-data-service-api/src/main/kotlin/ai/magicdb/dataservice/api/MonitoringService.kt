package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.ServiceCallStatistics
import ai.magicdb.dataservice.api.model.ServiceErrorStatistics
import ai.magicdb.dataservice.api.model.ServicePerformanceMetrics
import java.time.LocalDateTime

/**
 * 服务监控接口
 *
 * @author magicdb
 */
interface MonitoringService {
    /**
     * 记录服务调用
     *
     * @param serviceId 服务ID
     * @param executionTime 执行时间（毫秒）
     * @param success 是否成功
     * @param errorMessage 错误信息（如果失败）
     * @param userId 用户ID
     * @param clientIp 客户端IP
     */
    fun recordServiceCall(
        serviceId: String,
        executionTime: Long,
        success: Boolean,
        errorMessage: String? = null,
        userId: Long? = null,
        clientIp: String? = null
    )
    
    /**
     * 获取服务调用统计
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务调用统计
     */
    fun getServiceCallStatistics(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): List<ServiceCallStatistics>
    
    /**
     * 获取服务错误统计
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务错误统计
     */
    fun getServiceErrorStatistics(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): List<ServiceErrorStatistics>
    
    /**
     * 获取服务性能指标
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务性能指标
     */
    fun getServicePerformanceMetrics(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): List<ServicePerformanceMetrics>
    
    /**
     * 获取最近调用的服务
     *
     * @param limit 限制数量
     * @return 最近调用的服务ID列表
     */
    fun getRecentlyCalledServices(limit: Int = 10): List<String>
    
    /**
     * 获取调用最多的服务
     *
     * @param limit 限制数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 调用最多的服务ID列表
     */
    fun getMostCalledServices(
        limit: Int = 10,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): List<String>
    
    /**
     * 获取错误最多的服务
     *
     * @param limit 限制数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 错误最多的服务ID列表
     */
    fun getMostErrorServices(
        limit: Int = 10,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): List<String>
    
    /**
     * 获取性能最差的服务
     *
     * @param limit 限制数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 性能最差的服务ID列表
     */
    fun getWorstPerformanceServices(
        limit: Int = 10,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): List<String>
    
    /**
     * 清除监控数据
     *
     * @param serviceId 服务ID，如果为null则清除所有服务
     * @param before 清除此时间之前的数据
     * @return 清除的记录数
     */
    fun clearMonitoringData(
        serviceId: String? = null,
        before: LocalDateTime? = null
    ): Int
}
