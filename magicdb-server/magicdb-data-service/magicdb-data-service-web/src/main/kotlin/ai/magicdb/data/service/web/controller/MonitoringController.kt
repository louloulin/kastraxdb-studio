package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.MonitoringService
import ai.magicdb.data.service.api.model.ServiceCallStatistics
import ai.magicdb.data.service.api.model.ServiceErrorStatistics
import ai.magicdb.data.service.api.model.ServicePerformanceMetrics
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

/**
 * 监控控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/monitoring")
class MonitoringController(
    private val monitoringService: MonitoringService
) {
    private val logger = LoggerFactory.getLogger(MonitoringController::class.java)
    
    /**
     * 获取服务调用统计
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务调用统计
     */
    @GetMapping("/statistics")
    fun getServiceCallStatistics(
        @RequestParam(required = false) serviceId: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime?
    ): ListResult<ServiceCallStatistics> {
        logger.info("获取服务调用统计: serviceId={}, startTime={}, endTime={}", serviceId, startTime, endTime)
        val statistics = monitoringService.getServiceCallStatistics(serviceId, startTime, endTime)
        return ListResult.of(statistics)
    }
    
    /**
     * 获取服务错误统计
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务错误统计
     */
    @GetMapping("/errors")
    fun getServiceErrorStatistics(
        @RequestParam(required = false) serviceId: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime?
    ): ListResult<ServiceErrorStatistics> {
        logger.info("获取服务错误统计: serviceId={}, startTime={}, endTime={}", serviceId, startTime, endTime)
        val statistics = monitoringService.getServiceErrorStatistics(serviceId, startTime, endTime)
        return ListResult.of(statistics)
    }
    
    /**
     * 获取服务性能指标
     *
     * @param serviceId 服务ID，如果为null则获取所有服务
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务性能指标
     */
    @GetMapping("/performance")
    fun getServicePerformanceMetrics(
        @RequestParam(required = false) serviceId: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime?
    ): ListResult<ServicePerformanceMetrics> {
        logger.info("获取服务性能指标: serviceId={}, startTime={}, endTime={}", serviceId, startTime, endTime)
        val metrics = monitoringService.getServicePerformanceMetrics(serviceId, startTime, endTime)
        return ListResult.of(metrics)
    }
    
    /**
     * 获取最近调用的服务
     *
     * @param limit 限制数量
     * @return 最近调用的服务ID列表
     */
    @GetMapping("/recent-services")
    fun getRecentlyCalledServices(
        @RequestParam(defaultValue = "10") limit: Int
    ): ListResult<String> {
        logger.info("获取最近调用的服务: limit={}", limit)
        val services = monitoringService.getRecentlyCalledServices(limit)
        return ListResult.of(services)
    }
    
    /**
     * 获取调用最多的服务
     *
     * @param limit 限制数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 调用最多的服务ID列表
     */
    @GetMapping("/most-called-services")
    fun getMostCalledServices(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime?
    ): ListResult<String> {
        logger.info("获取调用最多的服务: limit={}, startTime={}, endTime={}", limit, startTime, endTime)
        val services = monitoringService.getMostCalledServices(limit, startTime, endTime)
        return ListResult.of(services)
    }
    
    /**
     * 获取错误最多的服务
     *
     * @param limit 限制数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 错误最多的服务ID列表
     */
    @GetMapping("/most-error-services")
    fun getMostErrorServices(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime?
    ): ListResult<String> {
        logger.info("获取错误最多的服务: limit={}, startTime={}, endTime={}", limit, startTime, endTime)
        val services = monitoringService.getMostErrorServices(limit, startTime, endTime)
        return ListResult.of(services)
    }
    
    /**
     * 获取性能最差的服务
     *
     * @param limit 限制数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 性能最差的服务ID列表
     */
    @GetMapping("/worst-performance-services")
    fun getWorstPerformanceServices(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime?
    ): ListResult<String> {
        logger.info("获取性能最差的服务: limit={}, startTime={}, endTime={}", limit, startTime, endTime)
        val services = monitoringService.getWorstPerformanceServices(limit, startTime, endTime)
        return ListResult.of(services)
    }
    
    /**
     * 清除监控数据
     *
     * @param serviceId 服务ID，如果为null则清除所有服务
     * @param before 清除此时间之前的数据
     * @return 清除的记录数
     */
    @DeleteMapping("/data")
    fun clearMonitoringData(
        @RequestParam(required = false) serviceId: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) before: LocalDateTime?
    ): DataResult<Int> {
        logger.info("清除监控数据: serviceId={}, before={}", serviceId, before)
        val count = monitoringService.clearMonitoringData(serviceId, before)
        return DataResult.of(count)
    }
    
    /**
     * 记录服务调用（内部使用）
     *
     * @param serviceId 服务ID
     * @param executionTime 执行时间（毫秒）
     * @param success 是否成功
     * @param errorMessage 错误信息（如果失败）
     * @param userId 用户ID
     * @param clientIp 客户端IP
     * @return 操作结果
     */
    @PostMapping("/record")
    fun recordServiceCall(
        @RequestParam serviceId: String,
        @RequestParam executionTime: Long,
        @RequestParam success: Boolean,
        @RequestParam(required = false) errorMessage: String?,
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) clientIp: String?
    ): ActionResult {
        logger.info("记录服务调用: serviceId={}, executionTime={}, success={}", serviceId, executionTime, success)
        monitoringService.recordServiceCall(serviceId, executionTime, success, errorMessage, userId, clientIp)
        return ActionResult.isSuccess()
    }
}
