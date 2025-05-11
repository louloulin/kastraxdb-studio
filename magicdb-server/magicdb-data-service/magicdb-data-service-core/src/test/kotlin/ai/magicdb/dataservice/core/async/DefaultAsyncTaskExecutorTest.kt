package ai.magicdb.dataservice.core.async

import ai.magicdb.dataservice.core.script.ScriptExecutor
import ai.magicdb.dataservice.api.model.AsyncTask
import ai.magicdb.dataservice.api.model.TaskStatus
import ai.magicdb.dataservice.api.model.TaskType
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
            createTime = Date()
        )

        // 提交任务
        val taskId = asyncTaskExecutor.submitTask(task)

        // 验证任务ID
        assertNotNull(taskId)

        // 等待任务完成
        waitForTaskCompletion(taskId)

        // 验证任务状态
        assertEquals(TaskStatus.COMPLETED, asyncTaskExecutor.getTaskStatus(taskId))

        // 验证任务结果
        assertEquals(42, asyncTaskExecutor.getTaskResult(taskId))
    }

    @Test
    fun testCancelTask() {
        // 准备测试数据
        val script = """
            // 长时间运行的脚本
            var result = 0;
            for (var i = 0; i < 1000000000; i++) {
                result += i;
            }
            return result;
        """.trimIndent()
        val language = "js"

        // 创建任务
        val task = AsyncTask(
            id = UUID.randomUUID().toString(),
            name = "测试取消任务",
            type = TaskType.SCRIPT_EXECUTION,
            parameters = mapOf(
                "script" to script,
                "language" to language,
                "context" to emptyMap<String, Any?>()
            ),
            createTime = Date()
        )

        // 提交任务
        val taskId = asyncTaskExecutor.submitTask(task)

        // 等待任务开始执行
        Thread.sleep(100)

        // 取消任务
        val result = asyncTaskExecutor.cancelTask(taskId)

        // 验证取消结果
        assertTrue(result)

        // 等待任务状态更新
        Thread.sleep(100)

        // 验证任务状态
        assertEquals(TaskStatus.CANCELLED, asyncTaskExecutor.getTaskStatus(taskId))
    }

    @Test
    fun testTaskTimeout() {
        // 准备测试数据
        val script = """
            // 长时间运行的脚本
            var result = 0;
            for (var i = 0; i < 1000000000; i++) {
                result += i;
            }
            return result;
        """.trimIndent()
        val language = "js"

        // 创建任务
        val task = AsyncTask(
            id = UUID.randomUUID().toString(),
            name = "测试超时任务",
            type = TaskType.SCRIPT_EXECUTION,
            parameters = mapOf(
                "script" to script,
                "language" to language,
                "context" to emptyMap<String, Any?>()
            ),
            timeout = 100, // 100毫秒超时
            createTime = Date()
        )

        // 提交任务
        val taskId = asyncTaskExecutor.submitTask(task)

        // 等待任务超时
        Thread.sleep(200)

        // 验证任务状态
        assertEquals(TaskStatus.TIMEOUT, asyncTaskExecutor.getTaskStatus(taskId))
    }

    @Test
    fun testGetAllTasks() {
        // 准备测试数据
        val script = "return 42;"
        val language = "js"

        // 模拟脚本执行
        `when`(scriptExecutor.execute(script, language, emptyMap())).thenReturn(42)

        // 创建任务
        val task1 = AsyncTask(
            id = UUID.randomUUID().toString(),
            name = "测试任务1",
            type = TaskType.SCRIPT_EXECUTION,
            parameters = mapOf(
                "script" to script,
                "language" to language,
                "context" to emptyMap<String, Any?>()
            ),
            createTime = Date()
        )

        val task2 = AsyncTask(
            id = UUID.randomUUID().toString(),
            name = "测试任务2",
            type = TaskType.SCRIPT_EXECUTION,
            parameters = mapOf(
                "script" to script,
                "language" to language,
                "context" to emptyMap<String, Any?>()
            ),
            createTime = Date()
        )

        // 提交任务
        val taskId1 = asyncTaskExecutor.submitTask(task1)
        val taskId2 = asyncTaskExecutor.submitTask(task2)

        // 等待任务完成
        waitForTaskCompletion(taskId1)
        waitForTaskCompletion(taskId2)

        // 获取所有任务
        val tasks = asyncTaskExecutor.getAllTasks()

        // 验证任务数量
        assertTrue(tasks.size >= 2)

        // 验证任务ID
        assertTrue(tasks.any { it.id == taskId1 })
        assertTrue(tasks.any { it.id == taskId2 })
    }

    @Test
    fun testCleanupCompletedTasks() {
        // 准备测试数据
        val script = "return 42;"
        val language = "js"

        // 模拟脚本执行
        `when`(scriptExecutor.execute(script, language, emptyMap())).thenReturn(42)

        // 创建任务
        val task = AsyncTask(
            id = UUID.randomUUID().toString(),
            name = "测试清理任务",
            type = TaskType.SCRIPT_EXECUTION,
            parameters = mapOf(
                "script" to script,
                "language" to language,
                "context" to emptyMap<String, Any?>()
            ),
            createTime = Date()
        )

        // 提交任务
        val taskId = asyncTaskExecutor.submitTask(task)

        // 等待任务完成
        waitForTaskCompletion(taskId)

        // 验证任务状态
        assertEquals(TaskStatus.COMPLETED, asyncTaskExecutor.getTaskStatus(taskId))

        // 清理已完成任务（设置为0毫秒，表示立即清理）
        val count = asyncTaskExecutor.cleanupCompletedTasks(0)

        // 验证清理结果
        assertTrue(count > 0)

        // 验证任务是否被清理
        try {
            asyncTaskExecutor.getTaskStatus(taskId)
            fail("任务应该已被清理")
        } catch (e: IllegalArgumentException) {
            // 预期异常
        }
    }

    /**
     * 等待任务完成
     */
    private fun waitForTaskCompletion(taskId: String) {
        var maxWaitTime = 5000L // 最大等待时间5秒
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
                fail("等待任务完成出错: ${e.message}")
            }
        }

        fail("等待任务完成超时")
    }
}
