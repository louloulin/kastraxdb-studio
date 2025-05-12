package ai.magicdb.data.service.core.async

import ai.magicdb.data.service.core.script.ScriptExecutor
import ai.magicdb.data.service.api.model.AsyncTask
import ai.magicdb.data.service.api.model.TaskStatus
import ai.magicdb.data.service.api.model.TaskType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.Date
import java.util.UUID

/**
 * 默认异步任务执行器测试
 *
 * @author magicdb
 */
class DefaultAsyncTaskExecutorTest {

    private lateinit var scriptExecutor: ScriptExecutor
    private lateinit var asyncTaskExecutor: DefaultAsyncTaskExecutor

    @BeforeEach
    fun setUp() {
        scriptExecutor = Mockito.mock(ScriptExecutor::class.java)
        asyncTaskExecutor = DefaultAsyncTaskExecutor(scriptExecutor)
    }

    @Test
    fun testSubmitScriptTask() {
        // 准备测试数据
        val script = "return 42;"
        val language = "js"
        val context = mapOf("foo" to "bar")

        // 模拟脚本执行
        `when`(scriptExecutor.execute(script, language, context)).thenReturn(42)

        // 创建任务
        val task = AsyncTask(
            id = UUID.randomUUID().toString(),
            name = "测试脚本任务",
            type = TaskType.SCRIPT_EXECUTION,
            parameters = mapOf(
                "script" to script,
                "language" to language,
                "context" to context
            ),
            priority = 1,
            timeout = 5000,
            createTime = Date()
        )

        // 提交任务
        val taskId = asyncTaskExecutor.submitTask(task)

        // 验证任务ID
        assertEquals(task.id, taskId)

        // 等待任务完成
        waitForTaskCompletion(taskId)

        // 验证任务状态
        val status = asyncTaskExecutor.getTaskStatus(taskId)
        assertEquals(TaskStatus.COMPLETED, status)

        // 验证任务结果
        val result = asyncTaskExecutor.getTaskResult(taskId)
        assertEquals(42, result)
    }

    @Test
    fun testSubmitDataImportTask() {
        // 准备测试数据
        val dataSource = "test-datasource"
        val tableName = "test-table"
        val data = listOf(
            mapOf("id" to 1, "name" to "Alice"),
            mapOf("id" to 2, "name" to "Bob")
        )

        // 创建任务
        val task = AsyncTask(
            id = UUID.randomUUID().toString(),
            name = "测试数据导入任务",
            type = TaskType.DATA_IMPORT,
            parameters = mapOf(
                "dataSource" to dataSource,
                "tableName" to tableName,
                "data" to data
            ),
            priority = 1,
            timeout = 5000,
            createTime = Date()
        )

        // 提交任务
        val taskId = asyncTaskExecutor.submitTask(task)

        // 验证任务ID
        assertEquals(task.id, taskId)

        // 等待任务完成
        waitForTaskCompletion(taskId)

        // 验证任务状态
        val status = asyncTaskExecutor.getTaskStatus(taskId)
        assertEquals(TaskStatus.COMPLETED, status)
    }

    @Test
    fun testCancelTask() {
        // 创建长时间运行的任务
        val task = AsyncTask(
            id = UUID.randomUUID().toString(),
            name = "测试取消任务",
            type = TaskType.OTHER,
            parameters = mapOf(
                "sleepTime" to 10000 // 10秒
            ),
            priority = 1,
            timeout = 20000,
            createTime = Date()
        )

        // 提交任务
        val taskId = asyncTaskExecutor.submitTask(task)

        // 等待任务开始执行
        Thread.sleep(500)

        // 取消任务
        val result = asyncTaskExecutor.cancelTask(taskId)
        assertTrue(result)

        // 验证任务状态
        val status = asyncTaskExecutor.getTaskStatus(taskId)
        assertEquals(TaskStatus.CANCELLED, status)
    }

    @Test
    fun testGetAllTasks() {
        // 创建多个任务
        val task1 = AsyncTask(
            id = UUID.randomUUID().toString(),
            name = "测试任务1",
            type = TaskType.OTHER,
            parameters = emptyMap(),
            priority = 1,
            timeout = 5000,
            createTime = Date()
        )

        val task2 = AsyncTask(
            id = UUID.randomUUID().toString(),
            name = "测试任务2",
            type = TaskType.OTHER,
            parameters = emptyMap(),
            priority = 2,
            timeout = 5000,
            createTime = Date()
        )

        // 提交任务
        asyncTaskExecutor.submitTask(task1)
        asyncTaskExecutor.submitTask(task2)

        // 获取所有任务
        val tasks = asyncTaskExecutor.getAllTasks()
        assertTrue(tasks.size >= 2)
        assertTrue(tasks.any { it.id == task1.id })
        assertTrue(tasks.any { it.id == task2.id })
    }

    /**
     * 等待任务完成
     */
    private fun waitForTaskCompletion(taskId: String) {
        val maxWaitTime = 5000L // 最大等待时间5秒
        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < maxWaitTime) {
            try {
                val status = asyncTaskExecutor.getTaskStatus(taskId)
                if (status == TaskStatus.COMPLETED ||
                    status == TaskStatus.FAILED ||
                    status == TaskStatus.CANCELLED ||
                    status == TaskStatus.TIMEOUT) {
                    return
                }
                Thread.sleep(100)
            } catch (e: Exception) {
                fail<Void>("等待任务完成出错: ${e.message}")
            }
        }

        fail<Void>("等待任务完成超时")
    }
}
