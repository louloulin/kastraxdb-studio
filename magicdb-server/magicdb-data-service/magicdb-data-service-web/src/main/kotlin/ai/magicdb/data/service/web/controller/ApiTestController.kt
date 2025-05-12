package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.ApiTestService
import ai.magicdb.data.service.api.model.ApiTestCase
import ai.magicdb.data.service.api.model.ApiTestResult
import ai.magicdb.server.tools.base.wrapper.result.DataResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * API 测试控制器
 */
@RestController
@RequestMapping("/api/data-service")
class ApiTestController(
    private val apiTestService: ApiTestService
) {
    
    /**
     * 执行 API 测试
     */
    @PostMapping("/{id}/test")
    fun executeTest(
        @PathVariable("id") serviceId: String,
        @RequestBody parameters: Map<String, Any?>
    ): DataResult<ApiTestResult> {
        val result = apiTestService.executeTest(serviceId, parameters)
        return DataResult.of(result)
    }
    
    /**
     * 验证参数
     */
    @PostMapping("/{id}/validate")
    fun validateParameters(
        @PathVariable("id") serviceId: String,
        @RequestBody parameters: Map<String, Any?>
    ): DataResult<Map<String, String>> {
        val errors = apiTestService.validateParameters(serviceId, parameters)
        return DataResult.of(errors)
    }
    
    /**
     * 保存测试用例
     */
    @PostMapping("/test-case")
    fun saveTestCase(@RequestBody testCase: ApiTestCase): DataResult<ApiTestCase> {
        val savedTestCase = apiTestService.saveTestCase(testCase)
        return DataResult.of(savedTestCase)
    }
    
    /**
     * 获取测试用例
     */
    @GetMapping("/test-case/{id}")
    fun getTestCase(@PathVariable("id") id: String): DataResult<ApiTestCase?> {
        val testCase = apiTestService.getTestCase(id)
        return DataResult.of(testCase)
    }
    
    /**
     * 获取服务的所有测试用例
     */
    @GetMapping("/{id}/test-cases")
    fun getTestCasesByService(@PathVariable("id") serviceId: String): DataResult<List<ApiTestCase>> {
        val testCases = apiTestService.getTestCasesByService(serviceId)
        return DataResult.of(testCases)
    }
    
    /**
     * 删除测试用例
     */
    @DeleteMapping("/test-case/{id}")
    fun deleteTestCase(@PathVariable("id") id: String): DataResult<Boolean> {
        val result = apiTestService.deleteTestCase(id)
        return DataResult.of(result)
    }
}
