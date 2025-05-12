package ai.magicdb.data.service.api

import ai.magicdb.data.service.api.model.ErrorStatistics
import ai.magicdb.data.service.api.model.ExecutionRecord
import ai.magicdb.data.service.api.model.ServiceStatistics
import java.util.Date

/**
 * 服务监控接口
 * 用于收集和查询服务执行的统计信息
 */
interface ServiceMonitoringService {
    /**
     * 记录服务执行
     *
     * @param serviceId 服务ID
     * @param executionTime 执行时间（毫秒）
     * @param success 是否成功
     * @param errorMessage 错误信息（如果失败）
     */
    fun recordExecution(serviceId: String, executionTime: Long, success: Boolean, errorMessage: String? = null)

    /**
     * 获取服务执行统计
     *
     * @param serviceId 服务ID，如果为null则返回所有服务的统计
     * @param startTime 开始时间，如果为null则不限制开始时间
     * @param endTime 结束时间，如果为null则不限制结束时间
     * @return 服务执行统计
     */
    fun getServiceStatistics(serviceId: String? = null, startTime: Date? = null, endTime: Date? = null): List<ServiceStatistics>

    /**
     * 获取服务执行历史
     *
     * @param serviceId 服务ID，如果为null则返回所有服务的执行历史
     * @param startTime 开始时间，如果为null则不限制开始时间
     * @param endTime 结束时间，如果为null则不限制结束时间
     * @param limit 限制返回的记录数，默认为100
     * @param offset 偏移量，默认为0
     * @return 服务执行历史
     */
    fun getExecutionHistory(
        serviceId: String? = null,
        startTime: Date? = null,
        endTime: Date? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<ExecutionRecord>

    /**
     * 获取服务错误统计
     *
     * @param serviceId 服务ID，如果为null则返回所有服务的错误统计
     * @param startTime 开始时间，如果为null则不限制开始时间
     * @param endTime 结束时间，如果为null则不限制结束时间
     * @return 服务错误统计
     */
    fun getErrorStatistics(serviceId: String? = null, startTime: Date? = null, endTime: Date? = null): List<ErrorStatistics>

    /**
     * 清除服务执行历史
     *
     * @param serviceId 服务ID，如果为null则清除所有服务的执行历史
     * @param before 清除此时间之前的记录，如果为null则清除所有记录
     * @return 清除的记录数
     */
    fun clearExecutionHistory(serviceId: String? = null, before: Date? = null): Int
}
