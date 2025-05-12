package ai.magicdb.dataservice.core.security

import ai.magicdb.dataservice.api.model.SecurityAuditRecord
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
class SecurityAuditServiceTest {
    
    @Autowired
    private lateinit var securityAuditService: DefaultSecurityAuditService
    
    @MockBean
    private lateinit var jdbcTemplate: JdbcTemplate
    
    @MockBean
    private lateinit var securityAuditExporter: SecurityAuditExporter
    
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
        
        // Mock JdbcTemplate.update to return 1 (success)
        `when`(jdbcTemplate.update(anyString(), any())).thenReturn(1)
        `when`(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(1)
    }
    
    @Test
    fun testRecordAudit() {
        // Act
        val id = securityAuditService.recordAudit(testRecord)
        
        // Assert
        assertNotNull(id)
        verify(jdbcTemplate, times(1)).update(
            contains("INSERT INTO security_audit_record"),
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
        )
    }
    
    @Test
    fun testGetAuditRecord() {
        // Arrange
        `when`(jdbcTemplate.queryForObject(
            contains("SELECT * FROM security_audit_record WHERE id = ?"),
            any<RowMapper<SecurityAuditRecord>>(),
            eq(testRecord.id)
        )).thenReturn(testRecord)
        
        // Act
        val result = securityAuditService.getAuditRecord(testRecord.id)
        
        // Assert
        assertNotNull(result)
        assertEquals(testRecord.id, result?.id)
        assertEquals(testRecord.operationType, result?.operationType)
        assertEquals(testRecord.targetType, result?.targetType)
        assertEquals(testRecord.targetId, result?.targetId)
    }
    
    @Test
    fun testQueryAuditRecords() {
        // Arrange
        val records = listOf(testRecord)
        `when`(jdbcTemplate.query(
            contains("SELECT * FROM security_audit_record"),
            any<RowMapper<SecurityAuditRecord>>(),
            any(), any()
        )).thenReturn(records)
        
        // Act
        val result = securityAuditService.queryAuditRecords(
            operationType = SecurityAuditRecord.OperationType.CREATE,
            page = 0,
            size = 10
        )
        
        // Assert
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(testRecord.id, result[0].id)
    }
    
    @Test
    fun testCountAuditRecords() {
        // Arrange
        `when`(jdbcTemplate.queryForObject(
            contains("SELECT COUNT(*) FROM security_audit_record"),
            eq(Long::class.java),
            any()
        )).thenReturn(1L)
        
        // Act
        val result = securityAuditService.countAuditRecords(
            operationType = SecurityAuditRecord.OperationType.CREATE
        )
        
        // Assert
        assertEquals(1L, result)
    }
    
    @Test
    fun testReviewAuditRecord() {
        // Act
        val result = securityAuditService.reviewAuditRecord(
            id = testRecord.id,
            reviewerId = "reviewer-id",
            reviewNotes = "Test review notes"
        )
        
        // Assert
        assertTrue(result)
        verify(jdbcTemplate, times(1)).update(
            contains("UPDATE security_audit_record SET reviewed = TRUE"),
            any(), any(), any(), any()
        )
    }
    
    @Test
    fun testBatchReviewAuditRecords() {
        // Arrange
        val ids = listOf(testRecord.id, UUID.randomUUID().toString())
        `when`(jdbcTemplate.update(
            contains("UPDATE security_audit_record SET reviewed = TRUE"),
            any(), any(), any(), any(), any()
        )).thenReturn(2)
        
        // Act
        val result = securityAuditService.batchReviewAuditRecords(
            ids = ids,
            reviewerId = "reviewer-id",
            reviewNotes = "Test review notes"
        )
        
        // Assert
        assertEquals(2, result)
    }
    
    @Test
    fun testDeleteAuditRecord() {
        // Act
        val result = securityAuditService.deleteAuditRecord(testRecord.id)
        
        // Assert
        assertTrue(result)
        verify(jdbcTemplate, times(1)).update(
            contains("DELETE FROM security_audit_record WHERE id = ?"),
            eq(testRecord.id)
        )
    }
    
    @Test
    fun testBatchDeleteAuditRecords() {
        // Arrange
        val ids = listOf(testRecord.id, UUID.randomUUID().toString())
        `when`(jdbcTemplate.update(
            contains("DELETE FROM security_audit_record WHERE id IN"),
            any(), any()
        )).thenReturn(2)
        
        // Act
        val result = securityAuditService.batchDeleteAuditRecords(ids)
        
        // Assert
        assertEquals(2, result)
    }
    
    @Test
    fun testCleanupAuditRecords() {
        // Arrange
        val beforeTime = LocalDateTime.now().minusDays(30)
        `when`(jdbcTemplate.update(
            contains("DELETE FROM security_audit_record WHERE operation_time < ?"),
            eq(beforeTime)
        )).thenReturn(5)
        
        // Act
        val result = securityAuditService.cleanupAuditRecords(beforeTime)
        
        // Assert
        assertEquals(5, result)
    }
    
    @Test
    fun testExportAuditRecords() {
        // Arrange
        val records = listOf(testRecord)
        val expectedContent = "test content".toByteArray()
        
        `when`(jdbcTemplate.query(
            contains("SELECT * FROM security_audit_record"),
            any<RowMapper<SecurityAuditRecord>>()
        )).thenReturn(records)
        
        `when`(securityAuditExporter.export(eq(records), eq("CSV"))).thenReturn(expectedContent)
        
        // Act
        val result = securityAuditService.exportAuditRecords(format = "CSV")
        
        // Assert
        assertArrayEquals(expectedContent, result)
    }
    
    @Test
    fun testGetAuditStatistics() {
        // Arrange
        val expectedStats = mapOf(
            "totalCount" to 10L,
            "operationTypeStats" to listOf(Pair("CREATE", 5L), Pair("READ", 3L), Pair("UPDATE", 2L)),
            "targetTypeStats" to listOf(Pair("DATA_SERVICE", 7L), Pair("USER", 3L)),
            "resultStats" to listOf(Pair("SUCCESS", 8L), Pair("FAILURE", 2L)),
            "riskLevelStats" to listOf(Pair("LOW", 6L), Pair("MEDIUM", 3L), Pair("HIGH", 1L))
        )
        
        `when`(jdbcTemplate.queryForObject(
            contains("SELECT COUNT(*) FROM security_audit_record"),
            eq(Long::class.java),
            any()
        )).thenReturn(10L)
        
        `when`(jdbcTemplate.query(
            contains("SELECT operation_type, COUNT(*) as count"),
            any<RowMapper<Pair<String, Long>>>(),
            any()
        )).thenReturn(listOf(Pair("CREATE", 5L), Pair("READ", 3L), Pair("UPDATE", 2L)))
        
        `when`(jdbcTemplate.query(
            contains("SELECT target_type, COUNT(*) as count"),
            any<RowMapper<Pair<String, Long>>>(),
            any()
        )).thenReturn(listOf(Pair("DATA_SERVICE", 7L), Pair("USER", 3L)))
        
        `when`(jdbcTemplate.query(
            contains("SELECT result, COUNT(*) as count"),
            any<RowMapper<Pair<String, Long>>>(),
            any()
        )).thenReturn(listOf(Pair("SUCCESS", 8L), Pair("FAILURE", 2L)))
        
        `when`(jdbcTemplate.query(
            contains("SELECT risk_level, COUNT(*) as count"),
            any<RowMapper<Pair<String, Long>>>(),
            any()
        )).thenReturn(listOf(Pair("LOW", 6L), Pair("MEDIUM", 3L), Pair("HIGH", 1L)))
        
        `when`(jdbcTemplate.query(
            contains("SELECT user_id, username, COUNT(*) as count"),
            any<RowMapper<Map<String, Any>>>(),
            any()
        )).thenReturn(emptyList())
        
        `when`(jdbcTemplate.query(
            contains("SELECT DATE(operation_time) as date, COUNT(*) as count"),
            any<RowMapper<Pair<String, Long>>>(),
            any()
        )).thenReturn(emptyList())
        
        // Act
        val result = securityAuditService.getAuditStatistics()
        
        // Assert
        assertNotNull(result)
        assertEquals(expectedStats["totalCount"], result["totalCount"])
        assertEquals(expectedStats["operationTypeStats"], result["operationTypeStats"])
        assertEquals(expectedStats["targetTypeStats"], result["targetTypeStats"])
        assertEquals(expectedStats["resultStats"], result["resultStats"])
        assertEquals(expectedStats["riskLevelStats"], result["riskLevelStats"])
    }
}
