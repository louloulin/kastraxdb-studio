package ai.magicdb.dataservice.core.mapper

import ai.magicdb.dataservice.core.entity.ScheduledTaskDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

/**
 * 定时任务Mapper接口
 *
 * @author magicdb
 */
@Mapper
interface ScheduledTaskMapper : BaseMapper<ScheduledTaskDO> {
    
    /**
     * 根据服务ID查询任务列表
     *
     * @param serviceId 服务ID
     * @return 任务列表
     */
    @Select("SELECT * FROM data_service_scheduled_task WHERE service_id = #{serviceId}")
    fun selectByServiceId(@Param("serviceId") serviceId: String): List<ScheduledTaskDO>
    
    /**
     * 根据状态查询任务列表
     *
     * @param status 任务状态
     * @return 任务列表
     */
    @Select("SELECT * FROM data_service_scheduled_task WHERE status = #{status}")
    fun selectByStatus(@Param("status") status: String): List<ScheduledTaskDO>
    
    /**
     * 根据分组查询任务列表
     *
     * @param group 任务分组
     * @return 任务列表
     */
    @Select("SELECT * FROM data_service_scheduled_task WHERE `group` = #{group}")
    fun selectByGroup(@Param("group") group: String): List<ScheduledTaskDO>
    
    /**
     * 根据状态和分组查询任务列表
     *
     * @param status 任务状态
     * @param group 任务分组
     * @return 任务列表
     */
    @Select("SELECT * FROM data_service_scheduled_task WHERE status = #{status} AND `group` = #{group}")
    fun selectByStatusAndGroup(@Param("status") status: String, @Param("group") group: String): List<ScheduledTaskDO>
    
    /**
     * 更新任务状态
     *
     * @param taskId 任务ID
     * @param status 任务状态
     * @return 影响行数
     */
    @Update("UPDATE data_service_scheduled_task SET status = #{status}, update_time = #{updateTime} WHERE id = #{taskId}")
    fun updateStatus(
        @Param("taskId") taskId: String,
        @Param("status") status: String,
        @Param("updateTime") updateTime: Long
    ): Int
    
    /**
     * 更新任务执行信息
     *
     * @param taskId 任务ID
     * @param lastExecuteTime 最后执行时间
     * @param nextExecuteTime 下次执行时间
     * @param executeCount 执行次数
     * @param lastExecuteResult 最后执行结果
     * @param lastExecuteMessage 最后执行消息
     * @param lastExecuteDuration 最后执行耗时
     * @return 影响行数
     */
    @Update("""
        UPDATE data_service_scheduled_task 
        SET last_execute_time = #{lastExecuteTime}, 
            next_execute_time = #{nextExecuteTime}, 
            execute_count = execute_count + 1,
            success_count = CASE WHEN #{lastExecuteResult} = 1 THEN success_count + 1 ELSE success_count END,
            fail_count = CASE WHEN #{lastExecuteResult} = 0 THEN fail_count + 1 ELSE fail_count END,
            last_execute_result = #{lastExecuteResult}, 
            last_execute_message = #{lastExecuteMessage}, 
            last_execute_duration = #{lastExecuteDuration},
            update_time = #{updateTime}
        WHERE id = #{taskId}
    """)
    fun updateExecutionInfo(
        @Param("taskId") taskId: String,
        @Param("lastExecuteTime") lastExecuteTime: Long,
        @Param("nextExecuteTime") nextExecuteTime: Long,
        @Param("lastExecuteResult") lastExecuteResult: Boolean,
        @Param("lastExecuteMessage") lastExecuteMessage: String?,
        @Param("lastExecuteDuration") lastExecuteDuration: Long,
        @Param("updateTime") updateTime: Long
    ): Int
    
    /**
     * 更新任务启用状态
     *
     * @param taskId 任务ID
     * @param enabled 是否启用
     * @return 影响行数
     */
    @Update("UPDATE data_service_scheduled_task SET enabled = #{enabled}, update_time = #{updateTime} WHERE id = #{taskId}")
    fun updateEnabled(
        @Param("taskId") taskId: String,
        @Param("enabled") enabled: Boolean,
        @Param("updateTime") updateTime: Long
    ): Int
}
