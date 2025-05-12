package ai.magicdb.dataservice.core.security

import ai.magicdb.dataservice.api.model.SecurityAuditRecord
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter

/**
 * 安全审计导出器
 *
 * @author magicdb
 */
@Component
class SecurityAuditExporter {
    
    private val logger = LoggerFactory.getLogger(SecurityAuditExporter::class.java)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    /**
     * 导出安全审计记录
     *
     * @param records 安全审计记录列表
     * @param format 导出格式，支持CSV、EXCEL、PDF
     * @return 导出的文件内容
     */
    fun export(records: List<SecurityAuditRecord>, format: String): ByteArray {
        return when (format.uppercase()) {
            "CSV" -> exportToCsv(records)
            "EXCEL" -> exportToExcel(records)
            "PDF" -> exportToPdf(records)
            else -> exportToCsv(records)
        }
    }
    
    /**
     * 导出为CSV
     *
     * @param records 安全审计记录列表
     * @return CSV内容
     */
    private fun exportToCsv(records: List<SecurityAuditRecord>): ByteArray {
        val sb = StringBuilder()
        
        // 添加CSV头
        sb.appendLine("ID,操作类型,目标类型,目标ID,用户ID,用户名,IP地址,操作时间,操作结果,风险级别,是否已审核,审核人ID,审核时间,审核备注,详情")
        
        // 添加数据行
        records.forEach { record ->
            sb.appendLine(
                "${escapeForCsv(record.id)}," +
                "${escapeForCsv(record.operationType.name)}," +
                "${escapeForCsv(record.targetType.name)}," +
                "${escapeForCsv(record.targetId)}," +
                "${escapeForCsv(record.userId)}," +
                "${escapeForCsv(record.username)}," +
                "${escapeForCsv(record.ipAddress)}," +
                "${escapeForCsv(record.operationTime.format(dateTimeFormatter))}," +
                "${escapeForCsv(record.result.name)}," +
                "${escapeForCsv(record.riskLevel.name)}," +
                "${record.reviewed}," +
                "${escapeForCsv(record.reviewerId ?: "")}," +
                "${escapeForCsv(record.reviewTime?.format(dateTimeFormatter) ?: "")}," +
                "${escapeForCsv(record.reviewNotes ?: "")}," +
                "${escapeForCsv(record.details ?: "")}"
            )
        }
        
        return sb.toString().toByteArray(Charsets.UTF_8)
    }
    
    /**
     * 导出为Excel
     *
     * @param records 安全审计记录列表
     * @return Excel内容
     */
    private fun exportToExcel(records: List<SecurityAuditRecord>): ByteArray {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("安全审计记录")
        
        // 创建标题样式
        val headerStyle = workbook.createCellStyle() as XSSFCellStyle
        headerStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        
        val font = workbook.createFont()
        font.bold = true
        headerStyle.setFont(font)
        
        // 创建标题行
        val headerRow = sheet.createRow(0)
        val headers = arrayOf(
            "ID", "操作类型", "目标类型", "目标ID", "用户ID", "用户名", "IP地址", "操作时间",
            "操作结果", "风险级别", "是否已审核", "审核人ID", "审核时间", "审核备注", "详情"
        )
        
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }
        
        // 添加数据行
        records.forEachIndexed { index, record ->
            val row = sheet.createRow(index + 1)
            
            row.createCell(0).setCellValue(record.id)
            row.createCell(1).setCellValue(record.operationType.name)
            row.createCell(2).setCellValue(record.targetType.name)
            row.createCell(3).setCellValue(record.targetId)
            row.createCell(4).setCellValue(record.userId)
            row.createCell(5).setCellValue(record.username)
            row.createCell(6).setCellValue(record.ipAddress)
            row.createCell(7).setCellValue(record.operationTime.format(dateTimeFormatter))
            row.createCell(8).setCellValue(record.result.name)
            row.createCell(9).setCellValue(record.riskLevel.name)
            row.createCell(10).setCellValue(record.reviewed.toString())
            row.createCell(11).setCellValue(record.reviewerId ?: "")
            row.createCell(12).setCellValue(record.reviewTime?.format(dateTimeFormatter) ?: "")
            row.createCell(13).setCellValue(record.reviewNotes ?: "")
            row.createCell(14).setCellValue(record.details ?: "")
        }
        
        // 自动调整列宽
        for (i in headers.indices) {
            sheet.autoSizeColumn(i)
        }
        
        // 写入输出流
        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()
        
        return outputStream.toByteArray()
    }
    
    /**
     * 导出为PDF
     *
     * @param records 安全审计记录列表
     * @return PDF内容
     */
    private fun exportToPdf(records: List<SecurityAuditRecord>): ByteArray {
        // 由于PDF生成需要额外的依赖，这里简单返回CSV格式
        logger.warn("PDF导出未实现，返回CSV格式")
        return exportToCsv(records)
    }
    
    /**
     * 转义CSV字段
     *
     * @param value 字段值
     * @return 转义后的值
     */
    private fun escapeForCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
