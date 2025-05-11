package ai.magicdb.dataservice.core.mapper

import ai.magicdb.dataservice.core.entity.NodeExecutionDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

/**
 * 节点执行记录Mapper接口
 *
 * @author magicdb
 */
@Mapper
interface NodeExecutionMapper : BaseMapper<NodeExecutionDO> {
    
    /**
     * 根据流程执行ID查询节点执行记录列表
     *
     * @param flowExecutionId 流程执行ID
     * @return 节点执行记录列表
     */
    @Select("SELECT * FROM data_service_node_execution WHERE flow_execution_id = #{flowExecutionId} ORDER BY start_time ASC")
    fun selectByFlowExecutionId(@Param("flowExecutionId") flowExecutionId: String): List<NodeExecutionDO>
    
    /**
     * 更新节点执行记录状态
     *
     * @param id 节点执行ID
     * @param status 执行状态
     * @return 影响行数
     */
    @Update("UPDATE data_service_node_execution SET status = #{status} WHERE id = #{id}")
    fun updateStatus(
        @Param("id") id: String,
        @Param("status") status: String
    ): Int
    
    /**
     * 更新节点执行记录结果
     *
     * @param id 节点执行ID
     * @param status 执行状态
     * @param result 执行结果
     * @param errorMessage 错误消息
     * @param endTime 结束时间
     * @param duration 执行耗时
     * @param output 输出数据
     * @return 影响行数
     */
    @Update("""
        UPDATE data_service_node_execution 
        SET status = #{status}, 
            result = #{result}, 
            error_message = #{errorMessage}, 
            end_time = #{endTime}, 
            duration = #{duration},
            output = #{output}
        WHERE id = #{id}
    """)
    fun updateResult(
        @Param("id") id: String,
        @Param("status") status: String,
        @Param("result") result: String?,
        @Param("errorMessage") errorMessage: String?,
        @Param("endTime") endTime: Long,
        @Param("duration") duration: Long,
        @Param("output") output: String?
    ): Int
}
