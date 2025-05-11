package ai.magicdb.dataservice.web.controller

import ai.magicdb.dataservice.api.ServiceOrchestrator
import ai.magicdb.dataservice.api.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * 服务流程控制器测试
 *
 * @author magicdb
 */
@WebMvcTest(ServiceFlowController::class)
class ServiceFlowControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockBean
    private lateinit var serviceOrchestrator: ServiceOrchestrator
    
    private lateinit var testFlow: ServiceFlow
    private lateinit var testExecution: ServiceFlowExecution
    
    @BeforeEach
    fun setUp() {
        // 创建测试流程
        testFlow = createTestFlow("test-flow")
        
        // 创建测试执行记录
        testExecution = createTestExecution("test-execution", "test-flow")
    }
    
    @Test
    fun testCreateFlow() {
        // 设置模拟对象的行为
        `when`(serviceOrchestrator.createFlow(any())).thenReturn("test-flow")
        
        // 执行请求
        mockMvc.perform(post("/api/data-service/flow")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testFlow)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("test-flow"))
        
        // 验证方法调用
        verify(serviceOrchestrator).createFlow(any())
    }
    
    @Test
    fun testUpdateFlow() {
        // 设置模拟对象的行为
        `when`(serviceOrchestrator.updateFlow(any())).thenReturn(true)
        
        // 执行请求
        mockMvc.perform(put("/api/data-service/flow")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testFlow)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
        
        // 验证方法调用
        verify(serviceOrchestrator).updateFlow(any())
    }
    
    @Test
    fun testDeleteFlow() {
        // 设置模拟对象的行为
        `when`(serviceOrchestrator.deleteFlow("test-flow")).thenReturn(true)
        
        // 执行请求
        mockMvc.perform(delete("/api/data-service/flow/test-flow")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
        
        // 验证方法调用
        verify(serviceOrchestrator).deleteFlow("test-flow")
    }
    
    @Test
    fun testGetFlow() {
        // 设置模拟对象的行为
        `when`(serviceOrchestrator.getFlow("test-flow")).thenReturn(testFlow)
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/flow/test-flow")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("test-flow"))
            .andExpect(jsonPath("$.data.name").value("测试流程"))
        
        // 验证方法调用
        verify(serviceOrchestrator).getFlow("test-flow")
    }
    
    @Test
    fun testGetAllFlows() {
        // 设置模拟对象的行为
        `when`(serviceOrchestrator.getAllFlows()).thenReturn(listOf(testFlow))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/flow")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("test-flow"))
            .andExpect(jsonPath("$.data[0].name").value("测试流程"))
        
        // 验证方法调用
        verify(serviceOrchestrator).getAllFlows()
    }
    
    @Test
    fun testExecuteFlow() {
        // 准备测试数据
        val parameters = mapOf("name" to "测试")
        
        // 设置模拟对象的行为
        `when`(serviceOrchestrator.executeFlow("test-flow", parameters)).thenReturn("test-execution")
        
        // 执行请求
        mockMvc.perform(post("/api/data-service/flow/test-flow/execute")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(parameters)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("test-execution"))
        
        // 验证方法调用
        verify(serviceOrchestrator).executeFlow("test-flow", parameters)
    }
    
    @Test
    fun testCancelExecution() {
        // 设置模拟对象的行为
        `when`(serviceOrchestrator.cancelExecution("test-execution")).thenReturn(true)
        
        // 执行请求
        mockMvc.perform(post("/api/data-service/flow/execution/test-execution/cancel")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
        
        // 验证方法调用
        verify(serviceOrchestrator).cancelExecution("test-execution")
    }
    
    @Test
    fun testGetExecutionStatus() {
        // 设置模拟对象的行为
        `when`(serviceOrchestrator.getExecutionStatus("test-execution")).thenReturn(testExecution)
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/flow/execution/test-execution")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("test-execution"))
            .andExpect(jsonPath("$.data.flowId").value("test-flow"))
        
        // 验证方法调用
        verify(serviceOrchestrator).getExecutionStatus("test-execution")
    }
    
    @Test
    fun testGetExecutionResult() {
        // 准备测试数据
        val result = mapOf("message" to "执行成功")
        
        // 设置模拟对象的行为
        `when`(serviceOrchestrator.getExecutionResult("test-execution")).thenReturn(result)
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/flow/execution/test-execution/result")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.message").value("执行成功"))
        
        // 验证方法调用
        verify(serviceOrchestrator).getExecutionResult("test-execution")
    }
    
    @Test
    fun testGetAllExecutions() {
        // 设置模拟对象的行为
        `when`(serviceOrchestrator.getAllExecutions()).thenReturn(listOf(testExecution))
        
        // 执行请求
        mockMvc.perform(get("/api/data-service/flow/execution")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("test-execution"))
            .andExpect(jsonPath("$.data[0].flowId").value("test-flow"))
        
        // 验证方法调用
        verify(serviceOrchestrator).getAllExecutions()
    }
    
    @Test
    fun testValidateFlow() {
        // 准备测试数据
        val errors = listOf("流程名称不能为空")
        
        // 设置模拟对象的行为
        `when`(serviceOrchestrator.validateFlow(any())).thenReturn(errors)
        
        // 执行请求
        mockMvc.perform(post("/api/data-service/flow/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testFlow)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0]").value("流程名称不能为空"))
        
        // 验证方法调用
        verify(serviceOrchestrator).validateFlow(any())
    }
    
    /**
     * 创建测试流程
     */
    private fun createTestFlow(id: String): ServiceFlow {
        // 创建开始节点
        val startNode = FlowNode(
            id = "start",
            name = "开始",
            type = FlowNodeType.START,
            x = 100,
            y = 100
        )
        
        // 创建服务节点
        val serviceNode = FlowNode(
            id = "service",
            name = "服务节点",
            type = FlowNodeType.SERVICE,
            x = 300,
            y = 100,
            config = mapOf(
                "serviceId" to "test-service",
                "parameters" to mapOf("name" to "测试")
            )
        )
        
        // 创建结束节点
        val endNode = FlowNode(
            id = "end",
            name = "结束",
            type = FlowNodeType.END,
            x = 500,
            y = 100
        )
        
        // 创建连接
        val connection1 = FlowConnection(
            id = "conn1",
            sourceId = "start",
            targetId = "service"
        )
        
        val connection2 = FlowConnection(
            id = "conn2",
            sourceId = "service",
            targetId = "end"
        )
        
        // 创建参数
        val parameter = FlowParameter(
            name = "name",
            type = "string",
            description = "名称参数",
            required = true
        )
        
        // 创建流程
        return ServiceFlow(
            id = id,
            name = "测试流程",
            description = "测试流程描述",
            nodes = listOf(startNode, serviceNode, endNode),
            connections = listOf(connection1, connection2),
            parameters = listOf(parameter),
            tags = listOf("test", "demo"),
            createTime = System.currentTimeMillis(),
            updateTime = System.currentTimeMillis()
        )
    }
    
    /**
     * 创建测试执行记录
     */
    private fun createTestExecution(id: String, flowId: String): ServiceFlowExecution {
        // 创建节点执行记录
        val nodeExecution1 = ServiceFlowNodeExecution(
            nodeId = "start",
            nodeName = "开始",
            status = ServiceFlowExecutionStatus.COMPLETED,
            startTime = System.currentTimeMillis() - 1000,
            endTime = System.currentTimeMillis() - 900,
            duration = 100
        )
        
        val nodeExecution2 = ServiceFlowNodeExecution(
            nodeId = "service",
            nodeName = "服务节点",
            status = ServiceFlowExecutionStatus.COMPLETED,
            startTime = System.currentTimeMillis() - 900,
            endTime = System.currentTimeMillis() - 500,
            duration = 400,
            result = mapOf("message" to "执行成功")
        )
        
        val nodeExecution3 = ServiceFlowNodeExecution(
            nodeId = "end",
            nodeName = "结束",
            status = ServiceFlowExecutionStatus.COMPLETED,
            startTime = System.currentTimeMillis() - 500,
            endTime = System.currentTimeMillis() - 400,
            duration = 100
        )
        
        // 创建执行记录
        return ServiceFlowExecution(
            id = id,
            flowId = flowId,
            flowName = "测试流程",
            status = ServiceFlowExecutionStatus.COMPLETED,
            startTime = System.currentTimeMillis() - 1000,
            endTime = System.currentTimeMillis() - 400,
            parameters = mapOf("name" to "测试"),
            result = mapOf("message" to "执行成功"),
            nodeExecutions = listOf(nodeExecution1, nodeExecution2, nodeExecution3),
            duration = 600
        )
    }
}
