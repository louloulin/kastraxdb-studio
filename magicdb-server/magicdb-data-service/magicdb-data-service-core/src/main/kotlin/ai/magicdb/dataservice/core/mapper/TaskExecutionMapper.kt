package ai.magicdb.dataservice.core.mapper

import ai.magicdb.dataservice.core.entity.TaskExecutionDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

/**
 * 任务执行记录Mapper接口
 *
 * @author magicdb
 */
@Mapper
interface TaskExecutionMapper : BaseMapper<TaskExecutionDO> {
    
    /**
     * 根据任务ID查询执行记录列表
     *
     * @param taskId 任务ID
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 执行记录列表
     */
    @Select("SELECT * FROM data_service_task_execution WHERE task_id = #{taskId} ORDER BY start_time DESC LIMIT #{limit} OFFSET #{offset}")
    fun selectByTaskId(
        @Param("taskId") taskId: String,
        @Param("limit") limit: Int,
        @Param("offset") offset: Int
    ): List<TaskExecutionDO>
    
    /**
     * 更新执行记录状态
     *
     * @param executionId 执行ID
     * @param status 执行状态
     * @return 影响行数
     */
    @Update("UPDATE data_service_task_execution SET status = #{status} WHERE id = #{executionId}")
    fun updateStatus(
        @Param("executionId") executionId: String,
        @Param("status") status: String
    ): Int
    
    /**
     * 更新执行记录结果
     *
     * @param executionId 执行ID
     * @param status 执行状态
     * @param result 执行结果
     * @param errorMessage 错误消息
     * @param endTime 结束时间
     * @param duration 执行耗时
     * @return 影响行数
     */
    @Update("""
        UPDATE data_service_task_execution 
        SET status = #{status}, 
            result = #{result}, 
            error_message = #{errorMessage}, 
            end_time = #{endTime}, 
            duration = #{duration}
        WHERE id = #{executionId}
    """)
    fun updateResult(
        @Param("executionId") executionId: String,
        @Param("status") status: String,
        @Param("result") result: String?,
        @Param("errorMessage") errorMessage: String?,
        @Param("endTime") endTime: Long,
        @Param("duration") duration: Long
    ): Int
    
    /**
     * 获取任务的最后一次执行记录
     *
     * @param taskId 任务ID
     * @return 执行记录
     */
    @Select("SELECT * FROM data_service_task_execution WHERE task_id = #{taskId} ORDER BY start_time DESC LIMIT 1")
    fun selectLastExecution(@Param("taskId") taskId: String): TaskExecutionDO?
    
    /**
     * 获取任务的执行记录数量
     *
     * @param taskId 任务ID
     * @return 执行记录数量
     */
    @Select("SELECT COUNT(*) FROM data_service_task_execution WHERE task_id = #{taskId}")
    fun countByTaskId(@Param("taskId") taskId: String): Int
    
    /**
     * 获取任务的成功执行记录数量
     *
     * @param taskId 任务ID
     * @return 成功执行记录数量
     */
    @Select("SELECT COUNT(*) FROM data_service_task_execution WHERE task_id = #{taskId} AND status = 'SUCCESS'")
    fun countSuccessByTaskId(@Param("taskId") taskId: String): Int
    
    /**
     * 获取任务的失败执行记录数量
     *
     * @param taskId 任务ID
     * @return 失败执行记录数量
     */
    @Select("SELECT COUNT(*) FROM data_service_task_execution WHERE task_id = #{taskId} AND status = 'FAILED'")
    fun countFailedByTaskId(@Param("taskId") taskId: String): Int
}
