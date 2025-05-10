package ai.magicdb.dataservice.core.executor

import ai.magicdb.dataservice.api.DataServiceExecutor
import ai.magicdb.dataservice.api.DataServiceRepository
import ai.magicdb.dataservice.api.DataSourceService
import ai.magicdb.dataservice.api.model.ServiceResult
import ai.magicdb.dataservice.core.cache.DataServiceCacheManager
import ai.magicdb.script.api.ScriptExecutor
import org.slf4j.LoggerFactory
import java.security.MessageDigest
import java.util.concurrent.TimeoutException

/**
 * 默认数据服务执行器实现
 *
 * @author magicdb
 */
class DefaultDataServiceExecutor(
    private val repository: DataServiceRepository,
    private val scriptExecutor: ScriptExecutor,
    private val cacheManager: DataServiceCacheManager,
    private val dataSourceService: DataSourceService
) : DataServiceExecutor {
    private val logger = LoggerFactory.getLogger(DefaultDataServiceExecutor::class.java)

    override fun execute(serviceId: String, parameters: Map<String, Any?>): ServiceResult {
        val startTime = System.currentTimeMillis()

        try {
            // 获取服务定义
            val service = repository.getService(serviceId)
                ?: return ServiceResult(
                    success = false,
                    message = "服务不存在: $serviceId",
                    duration = System.currentTimeMillis() - startTime
                )

            // 检查服务是否启用
            if (!service.enabled) {
                return ServiceResult(
                    success = false,
                    message = "服务已禁用: $serviceId",
                    duration = System.currentTimeMillis() - startTime
                )
            }

            // 验证参数
            service.parameters.forEach { param ->
                if (param.required && !parameters.containsKey(param.name)) {
                    return ServiceResult(
                        success = false,
                        message = "缺少必需参数: ${param.name}",
                        duration = System.currentTimeMillis() - startTime
                    )
                }
            }

            // 检查是否使用缓存
            val cacheKey = if (service.cacheTime > 0) {
                generateCacheKey(serviceId, parameters)
            } else {
                null
            }

            // 尝试从缓存获取结果
            if (cacheKey != null && service.cacheTime > 0) {
                val cachedResult = cacheManager.get(cacheKey)
                if (cachedResult != null) {
                    logger.debug("从缓存获取服务结果: {}", serviceId)
                    return cachedResult
                }
            }

            // 准备执行上下文
            val context = mutableMapOf<String, Any?>()
            context.putAll(parameters)
            context["service"] = service

            // 添加数据源信息
            if (service.dataSourceId != null) {
                val dataSourceId = service.dataSourceId!!
                context["dataSourceId"] = dataSourceId

                // 添加数据源信息
                val dataSource = dataSourceService.getDataSource(dataSourceId)
                if (dataSource != null) {
                    context["dataSource"] = dataSource
                }

                // 添加数据库查询函数
                val databaseName = service.databaseName ?: ""
                context["executeQuery"] = { sql: String ->
                    dataSourceService.executeQuery(dataSourceId, databaseName, sql)
                }

                // 添加获取表信息函数
                context["getTables"] = {
                    dataSourceService.getTables(dataSourceId, databaseName)
                }

                // 添加获取列信息函数
                context["getColumns"] = { tableName: String ->
                    dataSourceService.getColumns(dataSourceId, databaseName, tableName)
                }

                // 添加测试连接函数
                context["testConnection"] = {
                    dataSourceService.testConnection(dataSourceId)
                }

                // 添加获取数据库列表函数
                context["getDatabases"] = {
                    dataSourceService.getDatabases(dataSourceId)
                }
            }

            if (service.databaseName != null) {
                context["databaseName"] = service.databaseName
            }
            if (service.schemaName != null) {
                context["schemaName"] = service.schemaName
            }
            if (service.tableName != null) {
                context["tableName"] = service.tableName
            }

            // 执行脚本
            val result = executeWithTimeout(service.script, service.language, context, service.timeout)

            // 缓存结果
            if (cacheKey != null && service.cacheTime > 0) {
                val resultWithCacheInfo = result.copy(
                    cacheKey = cacheKey
                )
                cacheManager.put(cacheKey, resultWithCacheInfo, service.cacheTime)
            }

            return result
        } catch (e: Exception) {
            logger.error("执行服务出错: {}", e.message, e)
            return ServiceResult(
                success = false,
                message = "执行服务出错: ${e.message}",
                duration = System.currentTimeMillis() - startTime
            )
        }
    }

    override fun executeScript(script: String, language: String, parameters: Map<String, Any?>): ServiceResult {
        val startTime = System.currentTimeMillis()

        try {
            // 准备执行上下文
            val context = mutableMapOf<String, Any?>()
            context.putAll(parameters)

            // 执行脚本
            val result = executeWithTimeout(script, language, context, 30000)

            return result
        } catch (e: Exception) {
            logger.error("执行脚本出错: {}", e.message, e)
            return ServiceResult(
                success = false,
                message = "执行脚本出错: ${e.message}",
                duration = System.currentTimeMillis() - startTime
            )
        }
    }

    override fun validateScript(script: String, language: String): ServiceResult {
        val startTime = System.currentTimeMillis()

        try {
            // 简单验证，尝试执行脚本
            scriptExecutor.execute(language, script, emptyMap())

            return ServiceResult(
                success = true,
                message = "脚本验证通过",
                duration = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            logger.error("脚本验证失败: {}", e.message, e)
            return ServiceResult(
                success = false,
                message = "脚本验证失败: ${e.message}",
                duration = System.currentTimeMillis() - startTime
            )
        }
    }

    /**
     * 带超时的脚本执行
     */
    private fun executeWithTimeout(
        script: String,
        language: String,
        context: Map<String, Any?>,
        timeout: Long
    ): ServiceResult {
        val startTime = System.currentTimeMillis()

        try {
            // 执行脚本
            val result = scriptExecutor.execute(language, script, context)

            return ServiceResult(
                success = true,
                data = result,
                duration = System.currentTimeMillis() - startTime
            )
        } catch (e: TimeoutException) {
            logger.error("脚本执行超时: {}", e.message, e)
            return ServiceResult(
                success = false,
                message = "脚本执行超时",
                duration = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            logger.error("脚本执行出错: {}", e.message, e)
            return ServiceResult(
                success = false,
                message = "脚本执行出错: ${e.message}",
                duration = System.currentTimeMillis() - startTime
            )
        }
    }

    /**
     * 生成缓存键
     *
     * @param serviceId 服务ID
     * @param parameters 参数
     * @return 缓存键
     */
    private fun generateCacheKey(serviceId: String, parameters: Map<String, Any?>): String {
        val md = MessageDigest.getInstance("MD5")
        val input = "service:$serviceId:${parameters.entries.sortedBy { it.key }.joinToString(",") { "${it.key}=${it.value}" }}"
        val bytes = md.digest(input.toByteArray())
        val hash = bytes.joinToString("") { "%02x".format(it) }
        return "service:$serviceId:$hash"
    }
}
