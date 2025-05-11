package ai.magicdb.dataservice.core.transform

import ai.magicdb.dataservice.api.model.TransformationRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.util.Base64

/**
 * Excel 转换器测试
 *
 * @author magicdb
 */
class ExcelTransformerTest {
    private lateinit var excelTransformer: ExcelTransformer
    private lateinit var objectMapper: ObjectMapper
    
    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper()
        excelTransformer = ExcelTransformer(objectMapper)
    }
    
    @Test
    fun testGetSupportedSourceFormats() {
        val formats = excelTransformer.getSupportedSourceFormats()
        assertEquals(3, formats.size)
        assertTrue(formats.contains("excel"))
        assertTrue(formats.contains("xlsx"))
        assertTrue(formats.contains("xls"))
    }
    
    @Test
    fun testGetSupportedTargetFormats() {
        val formats = excelTransformer.getSupportedTargetFormats()
        assertEquals(7, formats.size)
        assertTrue(formats.contains("excel"))
        assertTrue(formats.contains("xlsx"))
        assertTrue(formats.contains("xls"))
        assertTrue(formats.contains("json"))
        assertTrue(formats.contains("xml"))
        assertTrue(formats.contains("csv"))
        assertTrue(formats.contains("yaml"))
    }
    
    @Test
    fun testGetRuleTemplate() {
        val template = excelTransformer.getRuleTemplate()
        assertNotNull(template)
        assertTrue(template.containsKey("fieldMappings"))
        assertTrue(template.containsKey("valueConverters"))
        assertTrue(template.containsKey("includeFields"))
        assertTrue(template.containsKey("excludeFields"))
        assertTrue(template.containsKey("sheetName"))
        assertTrue(template.containsKey("headerRow"))
    }
    
    @Test
    fun testValidateRules() {
        // 有效规则
        val validRules = mapOf(
            "fieldMappings" to mapOf(
                "name" to "username",
                "age" to "userAge"
            ),
            "valueConverters" to mapOf(
                "name" to mapOf(
                    "type" to "uppercase"
                )
            )
        )
        
        val validErrors = excelTransformer.validateRules(validRules)
        assertTrue(validErrors.isEmpty())
        
        // 无效规则 - 值转换器类型无效
        val invalidRules = mapOf(
            "valueConverters" to mapOf(
                "name" to mapOf(
                    "type" to "invalidType"
                )
            )
        )
        
        val invalidErrors = excelTransformer.validateRules(invalidRules)
        assertFalse(invalidErrors.isEmpty())
        assertTrue(invalidErrors[0].contains("不支持的值转换器类型"))
    }
    
    @Test
    fun testTransformExcelToJson() {
        // 创建测试 Excel 数据
        val excelData = createTestExcelData()
        
        // 创建转换请求
        val request = TransformationRequest(
            sourceData = excelData,
            sourceFormat = "excel",
            targetFormat = "json",
            options = mapOf("pretty" to true)
        )
        
        // 执行转换
        val result = excelTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("json", result.targetFormat)
        
        // 解析 JSON 结果
        val jsonNode = objectMapper.readTree(result.targetData as String)
        assertTrue(jsonNode.isArray)
        assertEquals(2, jsonNode.size())
        
        // 验证第一条记录
        val firstRecord = jsonNode[0]
        assertEquals("John", firstRecord.get("name").asText())
        assertEquals("30", firstRecord.get("age").asText())
        
        // 验证第二条记录
        val secondRecord = jsonNode[1]
        assertEquals("Alice", secondRecord.get("name").asText())
        assertEquals("25", secondRecord.get("age").asText())
    }
    
    @Test
    fun testTransformExcelToXml() {
        // 创建测试 Excel 数据
        val excelData = createTestExcelData()
        
        // 创建转换请求
        val request = TransformationRequest(
            sourceData = excelData,
            sourceFormat = "excel",
            targetFormat = "xml",
            options = mapOf("pretty" to true)
        )
        
        // 执行转换
        val result = excelTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("xml", result.targetFormat)
        
        // 验证 XML 结果
        val xmlString = result.targetData as String
        assertTrue(xmlString.contains("<name>John</name>"))
        assertTrue(xmlString.contains("<age>30</age>"))
        assertTrue(xmlString.contains("<name>Alice</name>"))
        assertTrue(xmlString.contains("<age>25</age>"))
    }
    
    @Test
    fun testTransformExcelToCsv() {
        // 创建测试 Excel 数据
        val excelData = createTestExcelData()
        
        // 创建转换请求
        val request = TransformationRequest(
            sourceData = excelData,
            sourceFormat = "excel",
            targetFormat = "csv"
        )
        
        // 执行转换
        val result = excelTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("csv", result.targetFormat)
        
        // 验证 CSV 结果
        val csvString = result.targetData as String
        val lines = csvString.trim().split("\n")
        assertEquals(3, lines.size)
        assertEquals("name,age", lines[0])
        assertEquals("John,30", lines[1])
        assertEquals("Alice,25", lines[2])
    }
    
    @Test
    fun testTransformExcelToYaml() {
        // 创建测试 Excel 数据
        val excelData = createTestExcelData()
        
        // 创建转换请求
        val request = TransformationRequest(
            sourceData = excelData,
            sourceFormat = "excel",
            targetFormat = "yaml"
        )
        
        // 执行转换
        val result = excelTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("yaml", result.targetFormat)
        
        // 验证 YAML 结果
        val yamlString = result.targetData as String
        assertTrue(yamlString.contains("name: John"))
        assertTrue(yamlString.contains("age: '30'"))
        assertTrue(yamlString.contains("name: Alice"))
        assertTrue(yamlString.contains("age: '25'"))
    }
    
    @Test
    fun testTransformExcelToExcel() {
        // 创建测试 Excel 数据
        val excelData = createTestExcelData()
        
        // 创建转换请求
        val request = TransformationRequest(
            sourceData = excelData,
            sourceFormat = "excel",
            targetFormat = "excel",
            rules = mapOf(
                "fieldMappings" to mapOf(
                    "name" to "username",
                    "age" to "userAge"
                ),
                "valueConverters" to mapOf(
                    "name" to mapOf(
                        "type" to "uppercase"
                    )
                )
            )
        )
        
        // 执行转换
        val result = excelTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("excel", result.targetFormat)
        
        // 解析 Excel 结果
        val base64String = result.targetData as String
        val bytes = Base64.getDecoder().decode(base64String)
        
        ByteArrayInputStream(bytes).use { inputStream ->
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)
            
            // 验证表头
            val headerRow = sheet.getRow(0)
            assertEquals("username", headerRow.getCell(0).stringCellValue)
            assertEquals("userAge", headerRow.getCell(1).stringCellValue)
            
            // 验证第一行数据
            val firstRow = sheet.getRow(1)
            assertEquals("JOHN", firstRow.getCell(0).stringCellValue)
            assertEquals("30", firstRow.getCell(1).stringCellValue)
            
            // 验证第二行数据
            val secondRow = sheet.getRow(2)
            assertEquals("ALICE", secondRow.getCell(0).stringCellValue)
            assertEquals("25", secondRow.getCell(1).stringCellValue)
        }
    }
    
    @Test
    fun testTransformWithFieldMapping() {
        // 创建测试 Excel 数据
        val excelData = createTestExcelData()
        
        // 创建转换请求
        val request = TransformationRequest(
            sourceData = excelData,
            sourceFormat = "excel",
            targetFormat = "json",
            rules = mapOf(
                "fieldMappings" to mapOf(
                    "name" to "username",
                    "age" to "userAge"
                )
            )
        )
        
        // 执行转换
        val result = excelTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("json", result.targetFormat)
        
        // 解析 JSON 结果
        val jsonNode = objectMapper.readTree(result.targetData as String)
        assertTrue(jsonNode.isArray)
        assertEquals(2, jsonNode.size())
        
        // 验证字段映射
        val firstRecord = jsonNode[0]
        assertTrue(firstRecord.has("username"))
        assertTrue(firstRecord.has("userAge"))
        assertFalse(firstRecord.has("name"))
        assertFalse(firstRecord.has("age"))
    }
    
    @Test
    fun testTransformWithValueConverter() {
        // 创建测试 Excel 数据
        val excelData = createTestExcelData()
        
        // 创建转换请求
        val request = TransformationRequest(
            sourceData = excelData,
            sourceFormat = "excel",
            targetFormat = "json",
            rules = mapOf(
                "valueConverters" to mapOf(
                    "name" to mapOf(
                        "type" to "uppercase"
                    )
                )
            )
        )
        
        // 执行转换
        val result = excelTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("json", result.targetFormat)
        
        // 解析 JSON 结果
        val jsonNode = objectMapper.readTree(result.targetData as String)
        assertTrue(jsonNode.isArray)
        assertEquals(2, jsonNode.size())
        
        // 验证值转换
        val firstRecord = jsonNode[0]
        assertEquals("JOHN", firstRecord.get("name").asText())
        assertEquals("30", firstRecord.get("age").asText())
    }
    
    @Test
    fun testTransformWithIncludeFields() {
        // 创建测试 Excel 数据
        val excelData = createTestExcelData()
        
        // 创建转换请求
        val request = TransformationRequest(
            sourceData = excelData,
            sourceFormat = "excel",
            targetFormat = "json",
            rules = mapOf(
                "includeFields" to listOf("name")
            )
        )
        
        // 执行转换
        val result = excelTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("json", result.targetFormat)
        
        // 解析 JSON 结果
        val jsonNode = objectMapper.readTree(result.targetData as String)
        assertTrue(jsonNode.isArray)
        assertEquals(2, jsonNode.size())
        
        // 验证包含字段
        val firstRecord = jsonNode[0]
        assertTrue(firstRecord.has("name"))
        assertFalse(firstRecord.has("age"))
    }
    
    @Test
    fun testTransformWithExcludeFields() {
        // 创建测试 Excel 数据
        val excelData = createTestExcelData()
        
        // 创建转换请求
        val request = TransformationRequest(
            sourceData = excelData,
            sourceFormat = "excel",
            targetFormat = "json",
            rules = mapOf(
                "excludeFields" to listOf("age")
            )
        )
        
        // 执行转换
        val result = excelTransformer.transform(request)
        
        // 验证结果
        assertTrue(result.success)
        assertNotNull(result.targetData)
        assertEquals("json", result.targetFormat)
        
        // 解析 JSON 结果
        val jsonNode = objectMapper.readTree(result.targetData as String)
        assertTrue(jsonNode.isArray)
        assertEquals(2, jsonNode.size())
        
        // 验证排除字段
        val firstRecord = jsonNode[0]
        assertTrue(firstRecord.has("name"))
        assertFalse(firstRecord.has("age"))
    }
    
    /**
     * 创建测试 Excel 数据
     */
    private fun createTestExcelData(): ByteArray {
        val workbook = WorkbookFactory.create(true)
        val sheet = workbook.createSheet("Sheet1")
        
        // 创建表头行
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("name")
        headerRow.createCell(1).setCellValue("age")
        
        // 创建数据行
        val row1 = sheet.createRow(1)
        row1.createCell(0).setCellValue("John")
        row1.createCell(1).setCellValue("30")
        
        val row2 = sheet.createRow(2)
        row2.createCell(0).setCellValue("Alice")
        row2.createCell(1).setCellValue("25")
        
        // 转换为字节数组
        val outputStream = java.io.ByteArrayOutputStream()
        workbook.write(outputStream)
        return outputStream.toByteArray()
    }
}
