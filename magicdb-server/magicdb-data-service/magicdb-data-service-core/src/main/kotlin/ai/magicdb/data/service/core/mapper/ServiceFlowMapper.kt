package ai.magicdb.data.service.core.mapper

import ai.magicdb.data.service.core.entity.ServiceFlowDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

/**
 * 服务流程Mapper接口
 *
 * @author magicdb
 */
@Mapper
interface ServiceFlowMapper : BaseMapper<ServiceFlowDO> {
    
    /**
     * 根据分组查询流程列表
     *
     * @param group 分组
     * @return 流程列表
     */
    @Select("SELECT * FROM data_service_flow WHERE `group` = #{group}")
    fun selectByGroup(@Param("group") group: String): List<ServiceFlowDO>
    
    /**
     * 更新流程执行信息
     *
     * @param flowId 流程ID
     * @param lastExecuteTime 最后执行时间
     * @param executeCount 执行次数
     * @param lastExecuteResult 最后执行结果
     * @param lastExecuteMessage 最后执行消息
     * @param lastExecuteDuration 最后执行耗时
     * @return 影响行数
     */
    @Update("""
        UPDATE data_service_flow 
        SET last_execute_time = #{lastExecuteTime}, 
            execute_count = execute_count + 1,
            success_count = CASE WHEN #{lastExecuteResult} = 1 THEN success_count + 1 ELSE success_count END,
            fail_count = CASE WHEN #{lastExecuteResult} = 0 THEN fail_count + 1 ELSE fail_count END,
            last_execute_result = #{lastExecuteResult}, 
            last_execute_message = #{lastExecuteMessage}, 
            last_execute_duration = #{lastExecuteDuration},
            update_time = #{updateTime}
        WHERE id = #{flowId}
    """)
    fun updateExecutionInfo(
        @Param("flowId") flowId: String,
        @Param("lastExecuteTime") lastExecuteTime: Long,
        @Param("lastExecuteResult") lastExecuteResult: Boolean,
        @Param("lastExecuteMessage") lastExecuteMessage: String?,
        @Param("lastExecuteDuration") lastExecuteDuration: Long,
        @Param("updateTime") updateTime: Long
    ): Int
    
    /**
     * 更新流程启用状态
     *
     * @param flowId 流程ID
     * @param enabled 是否启用
     * @return 影响行数
     */
    @Update("UPDATE data_service_flow SET enabled = #{enabled}, update_time = #{updateTime} WHERE id = #{flowId}")
    fun updateEnabled(
        @Param("flowId") flowId: String,
        @Param("enabled") enabled: Boolean,
        @Param("updateTime") updateTime: Long
    ): Int
}
