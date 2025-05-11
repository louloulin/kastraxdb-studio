package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.ServiceTestManager
import ai.magicdb.dataservice.api.model.ServiceTest
import ai.magicdb.dataservice.api.model.ServiceTestResult
import ai.magicdb.server.tools.base.wrapper.result.ActionResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import ai.magicdb.server.tools.base.wrapper.result.ListResult
import org.springframework.web.bind.annotation.*

/**
 * 服务测试控制器
 *
 * @author magicdb
 */
@RestController
@RequestMapping("/api/data-service/test")
class ServiceTestController(private val testManager: ServiceTestManager) {

    /**
     * 创建测试
     *
     * @param test 测试信息
     * @return 测试ID
     */
    @PostMapping
    fun createTest(@RequestBody test: ServiceTest): DataResult<String> {
        val testId = testManager.createTest(test)
        return DataResult.of(testId)
    }

    /**
     * 更新测试
     *
     * @param test 测试信息
     * @return 是否成功
     */
    @PutMapping
    fun updateTest(@RequestBody test: ServiceTest): ActionResult {
        val success = testManager.updateTest(test)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "更新测试失败", "")
        }
    }

    /**
     * 删除测试
     *
     * @param testId 测试ID
     * @return 是否成功
     */
    @DeleteMapping("/{testId}")
    fun deleteTest(@PathVariable testId: String): ActionResult {
        val success = testManager.deleteTest(testId)
        return if (success) {
            ActionResult.isSuccess()
        } else {
            ActionResult.fail("common.error", "删除测试失败", "")
        }
    }

    /**
     * 获取测试
     *
     * @param testId 测试ID
     * @return 测试信息
     */
    @GetMapping("/{testId}")
    fun getTest(@PathVariable testId: String): DataResult<ServiceTest> {
        val test = testManager.getTest(testId)
        return if (test != null) {
            DataResult.of(test)
        } else {
            DataResult.empty()
        }
    }

    /**
     * 获取服务的所有测试
     *
     * @param serviceId 服务ID
     * @return 测试列表
     */
    @GetMapping("/service/{serviceId}")
    fun getTestsByService(@PathVariable serviceId: String): ListResult<ServiceTest> {
        val tests = testManager.getTestsByService(serviceId)
        return ListResult.of(tests)
    }

    /**
     * 执行测试
     *
     * @param testId 测试ID
     * @return 测试结果
     */
    @PostMapping("/{testId}/run")
    fun runTest(@PathVariable testId: String): DataResult<ServiceTestResult> {
        val result = testManager.runTest(testId)
        return DataResult.of(result)
    }

    /**
     * 执行服务的所有测试
     *
     * @param serviceId 服务ID
     * @return 测试结果列表
     */
    @PostMapping("/service/{serviceId}/run")
    fun runTestsByService(@PathVariable serviceId: String): ListResult<ServiceTestResult> {
        val results = testManager.runTestsByService(serviceId)
        return ListResult.of(results)
    }

    /**
     * 获取测试结果
     *
     * @param testId 测试ID
     * @return 测试结果
     */
    @GetMapping("/{testId}/result")
    fun getTestResult(@PathVariable testId: String): DataResult<ServiceTestResult> {
        val result = testManager.getTestResult(testId)
        return if (result != null) {
            DataResult.of(result)
        } else {
            DataResult.empty()
        }
    }

    /**
     * 获取服务的所有测试结果
     *
     * @param serviceId 服务ID
     * @return 测试结果列表
     */
    @GetMapping("/service/{serviceId}/results")
    fun getTestResultsByService(@PathVariable serviceId: String): ListResult<ServiceTestResult> {
        val results = testManager.getTestResultsByService(serviceId)
        return ListResult.of(results)
    }
}
