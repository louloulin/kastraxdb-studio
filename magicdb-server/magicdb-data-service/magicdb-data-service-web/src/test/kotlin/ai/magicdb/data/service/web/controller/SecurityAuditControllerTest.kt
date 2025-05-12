package ai.magicdb.data.service.web.controller

import ai.magicdb.data.service.api.SecurityAuditService
import ai.magicdb.data.service.api.model.SecurityAuditRecord
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
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(SecurityAuditController::class)
class SecurityAuditControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockBean
    private lateinit var securityAuditService: SecurityAuditService
    
    private lateinit var testRecord: SecurityAuditRecord
    
    @BeforeEach
    fun setUp() {
        testRecord = SecurityAuditRecord(
            id = UUID.randomUUID().toString(),
            operationType = SecurityAuditRecord.OperationType.CREATE,
            targetType = SecurityAuditRecord.TargetType.DATA_SERVICE,
            targetId = "test-service-id",
            userId = "test-user-id",
            username = "testuser",
            ipAddress = "127.0.0.1",
            operationTime = LocalDateTime.now(),
            details = "Test details",
            result = SecurityAuditRecord.OperationResult.SUCCESS,
            riskLevel = SecurityAuditRecord.RiskLevel.LOW
        )
    }
    
    @Test
    fun testRecordAudit() {
        // Arrange
        `when`(securityAuditService.recordAudit(any())).thenReturn(testRecord.id)
        
        // Act & Assert
        mockMvc.perform(
            post("/api/data-service/security/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRecord))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(testRecord.id))
    }
    
    @Test
    fun testGetAuditRecord() {
        // Arrange
        `when`(securityAuditService.getAuditRecord(testRecord.id)).thenReturn(testRecord)
        
        // Act & Assert
        mockMvc.perform(get("/api/data-service/security/audit/${testRecord.id}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(testRecord.id))
            .andExpect(jsonPath("$.data.operationType").value(testRecord.operationType.name))
            .andExpect(jsonPath("$.data.targetType").value(testRecord.targetType.name))
            .andExpect(jsonPath("$.data.targetId").value(testRecord.targetId))
    }
    
    @Test
    fun testQueryAuditRecords() {
        // Arrange
        val records = listOf(testRecord)
        `when`(securityAuditService.queryAuditRecords(
            operationType = SecurityAuditRecord.OperationType.CREATE,
            page = 0,
            size = 20
        )).thenReturn(records)
        
        `when`(securityAuditService.countAuditRecords(
            operationType = SecurityAuditRecord.OperationType.CREATE
        )).thenReturn(1L)
        
        // Act & Assert
        mockMvc.perform(
            get("/api/data-service/security/audit/list")
                .param("operationType", "CREATE")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].id").value(testRecord.id))
            .andExpect(jsonPath("$.total").value(1))
    }
    
    @Test
    fun testReviewAuditRecord() {
        // Arrange
        `when`(securityAuditService.reviewAuditRecord(
            id = testRecord.id,
            reviewerId = "reviewer-id",
            reviewNotes = "Test review notes"
        )).thenReturn(true)
        
        // Act & Assert
        mockMvc.perform(
            post("/api/data-service/security/audit/${testRecord.id}/review")
                .param("reviewerId", "reviewer-id")
                .param("reviewNotes", "Test review notes")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
    }
    
    @Test
    fun testBatchReviewAuditRecords() {
        // Arrange
        val ids = listOf(testRecord.id, UUID.randomUUID().toString())
        `when`(securityAuditService.batchReviewAuditRecords(
            ids = ids,
            reviewerId = "reviewer-id",
            reviewNotes = "Test review notes"
        )).thenReturn(2)
        
        // Act & Assert
        mockMvc.perform(
            post("/api/data-service/security/audit/batch-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids))
                .param("reviewerId", "reviewer-id")
                .param("reviewNotes", "Test review notes")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(2))
    }
    
    @Test
    fun testDeleteAuditRecord() {
        // Arrange
        `when`(securityAuditService.deleteAuditRecord(testRecord.id)).thenReturn(true)
        
        // Act & Assert
        mockMvc.perform(delete("/api/data-service/security/audit/${testRecord.id}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
    }
    
    @Test
    fun testBatchDeleteAuditRecords() {
        // Arrange
        val ids = listOf(testRecord.id, UUID.randomUUID().toString())
        `when`(securityAuditService.batchDeleteAuditRecords(ids)).thenReturn(2)
        
        // Act & Assert
        mockMvc.perform(
            delete("/api/data-service/security/audit/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(2))
    }
    
    @Test
    fun testCleanupAuditRecords() {
        // Arrange
        val beforeTime = LocalDateTime.now().minusDays(30)
        `when`(securityAuditService.cleanupAuditRecords(any())).thenReturn(5)
        
        // Act & Assert
        mockMvc.perform(
            delete("/api/data-service/security/audit/cleanup")
                .param("beforeTime", beforeTime.toString())
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(5))
    }
    
    @Test
    fun testGetAuditStatistics() {
        // Arrange
        val statistics = mapOf(
            "totalCount" to 10L,
            "operationTypeStats" to listOf(Pair("CREATE", 5L), Pair("READ", 3L), Pair("UPDATE", 2L))
        )
        `when`(securityAuditService.getAuditStatistics(null, null)).thenReturn(statistics)
        
        // Act & Assert
        mockMvc.perform(get("/api/data-service/security/audit/statistics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalCount").value(10))
    }
}
