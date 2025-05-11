package ai.magicdb.dataservice.api

import ai.magicdb.dataservice.api.model.ServiceCallRecord
import ai.magicdb.dataservice.api.model.ServicePerformanceRecord
import java.time.LocalDateTime

/**
 * 监控存储库接口
 *
 * @author magicdb
 */
interface MonitoringRepository {
    /**
     * 保存服务调用记录
     *
     * @param record 调用记录
     * @return 记录ID
     */
    fun saveCallRecord(record: ServiceCallRecord): String

    /**
     * 获取服务调用记录
     *
     * @param recordId 记录ID
     * @return 调用记录
     */
    fun getCallRecord(recordId: String): ServiceCallRecord?

    /**
     * 获取服务调用记录列表
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 是否成功
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 调用记录列表
     */
    fun getCallRecords(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        success: Boolean? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<ServiceCallRecord>

    /**
     * 获取服务调用记录数量
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 是否成功
     * @return 调用记录数量
     */
    fun getCallRecordCount(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        success: Boolean? = null
    ): Long

    /**
     * 获取服务调用成功次数
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 成功次数
     */
    fun getSuccessCallCount(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Long

    /**
     * 获取服务调用失败次数
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 失败次数
     */
    fun getFailedCallCount(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Long

    /**
     * 获取服务平均执行时间
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 是否成功
     * @return 平均执行时间
     */
    fun getAvgExecutionTime(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        success: Boolean? = null
    ): Double

    /**
     * 获取服务最大执行时间
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 是否成功
     * @return 最大执行时间
     */
    fun getMaxExecutionTime(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        success: Boolean? = null
    ): Long

    /**
     * 获取服务最小执行时间
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 是否成功
     * @return 最小执行时间
     */
    fun getMinExecutionTime(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        success: Boolean? = null
    ): Long

    /**
     * 获取服务最后调用时间
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @return 最后调用时间
     */
    fun getLastCallTime(serviceId: String? = null): LocalDateTime?

    /**
     * 获取服务调用用户数
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 调用用户数
     */
    fun getUniqueUserCount(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Int

    /**
     * 获取服务调用IP数
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 调用IP数
     */
    fun getUniqueIpCount(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Int

    /**
     * 获取服务错误类型统计
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 错误类型统计
     */
    fun getErrorTypeStats(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Map<String, Long>

    /**
     * 获取服务最常见的错误消息
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 最常见的错误消息
     */
    fun getMostCommonErrors(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        limit: Int = 10
    ): List<Pair<String, Long>>

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
    ): List<Pair<String, Long>>

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
    ): List<Pair<String, Long>>

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
    ): List<Pair<String, Double>>

    /**
     * 获取按小时统计的调用次数
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 按小时统计的调用次数
     */
    fun getCallsByHour(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Map<Int, Long>

    /**
     * 获取按天统计的调用次数
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 按天统计的调用次数
     */
    fun getCallsByDay(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Map<String, Long>

    /**
     * 获取按小时统计的错误次数
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 按小时统计的错误次数
     */
    fun getErrorsByHour(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Map<Int, Long>

    /**
     * 获取按天统计的错误次数
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 按天统计的错误次数
     */
    fun getErrorsByDay(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null
    ): Map<String, Long>

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

    /**
     * 获取服务最后错误时间
     *
     * @param serviceId 服务ID
     * @return 最后错误时间
     */
    fun getLastErrorTime(serviceId: String): LocalDateTime?

    /**
     * 保存服务性能记录
     *
     * @param record 性能记录
     * @return 记录ID
     */
    fun savePerformanceRecord(record: ServicePerformanceRecord): String

    /**
     * 获取服务性能记录
     *
     * @param recordId 记录ID
     * @return 性能记录
     */
    fun getPerformanceRecord(recordId: String): ServicePerformanceRecord?

    /**
     * 获取服务性能记录列表
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 性能记录列表
     */
    fun getPerformanceRecords(
        serviceId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<ServicePerformanceRecord>
}
