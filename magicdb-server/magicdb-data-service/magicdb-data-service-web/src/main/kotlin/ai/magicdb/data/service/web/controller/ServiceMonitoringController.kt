package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.ServiceMonitoringService
import ai.magicdb.data.service.api.model.ErrorStatistics
import ai.magicdb.data.service.api.model.ExecutionRecord
import ai.magicdb.data.service.api.model.ServiceStatistics
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date

/**
 * 服务监控控制器
 */
@RestController
@RequestMapping("/api/data-service/monitoring")
class ServiceMonitoringController(
    private val serviceMonitoringService: ServiceMonitoringService
) {
    private val logger = LoggerFactory.getLogger(ServiceMonitoringController::class.java)

    /**
     * 获取服务统计
     */
    @GetMapping("/statistics")
    fun getServiceStatistics(
        @RequestParam(required = false) serviceId: String?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") startTime: Date?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") endTime: Date?
    ): DataResult<List<ServiceStatistics>> {
        try {
            val statistics = serviceMonitoringService.getServiceStatistics(serviceId, startTime, endTime)
            return DataResult.of(statistics)
        } catch (e: Exception) {
            logger.error("获取服务统计失败", e)
            return DataResult.error("GET_STATISTICS_FAILED", "获取服务统计失败: ${e.message}")
        }
    }

    /**
     * 获取执行历史
     */
    @GetMapping("/history")
    fun getExecutionHistory(
        @RequestParam(required = false) serviceId: String?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") startTime: Date?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") endTime: Date?,
        @RequestParam(defaultValue = "100") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): DataResult<List<ExecutionRecord>> {
        try {
            val history = serviceMonitoringService.getExecutionHistory(serviceId, startTime, endTime, limit, offset)
            return DataResult.of(history)
        } catch (e: Exception) {
            logger.error("获取执行历史失败", e)
            return DataResult.error("GET_HISTORY_FAILED", "获取执行历史失败: ${e.message}")
        }
    }

    /**
     * 获取错误统计
     */
    @GetMapping("/errors")
    fun getErrorStatistics(
        @RequestParam(required = false) serviceId: String?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") startTime: Date?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") endTime: Date?
    ): DataResult<List<ErrorStatistics>> {
        try {
            val errors = serviceMonitoringService.getErrorStatistics(serviceId, startTime, endTime)
            return DataResult.of(errors)
        } catch (e: Exception) {
            logger.error("获取错误统计失败", e)
            return DataResult.error("GET_ERRORS_FAILED", "获取错误统计失败: ${e.message}")
        }
    }

    /**
     * 清除执行历史
     */
    @DeleteMapping("/history")
    fun clearExecutionHistory(
        @RequestParam(required = false) serviceId: String?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") before: Date?
    ): DataResult<Int> {
        try {
            val count = serviceMonitoringService.clearExecutionHistory(serviceId, before)
            return DataResult.of(count)
        } catch (e: Exception) {
            logger.error("清除执行历史失败", e)
            return DataResult.error("CLEAR_HISTORY_FAILED", "清除执行历史失败: ${e.message}")
        }
    }
}
