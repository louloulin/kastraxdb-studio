package ai.magicdb.data.service.core.mapper

import ai.magicdb.data.service.core.entity.ServiceCallStatsDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import java.time.LocalDateTime

/**
 * 服务调用统计Mapper
 *
 * @author magicdb
 */
@Mapper
interface ServiceCallStatsMapper : BaseMapper<ServiceCallStatsDO> {
    
    /**
     * 获取服务调用统计
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务调用统计
     */
    @Select("""
        <script>
            SELECT * FROM data_service_call_stats
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND stats_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND stats_time &lt;= #{endTime}
                </if>
            </where>
            ORDER BY stats_time DESC
        </script>
    """)
    fun getServiceCallStats(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<ServiceCallStatsDO>
    
    /**
     * 获取服务调用统计（按天）
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务调用统计
     */
    @Select("""
        <script>
            SELECT 
                service_id,
                service_name,
                DATE_FORMAT(stats_time, '%Y-%m-%d') as stats_day,
                SUM(total_calls) as total_calls,
                SUM(success_calls) as success_calls,
                SUM(failed_calls) as failed_calls,
                SUM(success_calls) / SUM(total_calls) as success_rate,
                AVG(avg_execution_time) as avg_execution_time,
                MAX(max_execution_time) as max_execution_time,
                MIN(min_execution_time) as min_execution_time,
                COUNT(DISTINCT unique_users) as unique_users,
                COUNT(DISTINCT unique_ips) as unique_ips
            FROM data_service_call_stats
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND stats_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND stats_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY service_id, service_name, DATE_FORMAT(stats_time, '%Y-%m-%d')
            ORDER BY stats_day DESC
        </script>
    """)
    fun getServiceCallStatsByDay(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 获取服务调用统计（按月）
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务调用统计
     */
    @Select("""
        <script>
            SELECT 
                service_id,
                service_name,
                DATE_FORMAT(stats_time, '%Y-%m') as stats_month,
                SUM(total_calls) as total_calls,
                SUM(success_calls) as success_calls,
                SUM(failed_calls) as failed_calls,
                SUM(success_calls) / SUM(total_calls) as success_rate,
                AVG(avg_execution_time) as avg_execution_time,
                MAX(max_execution_time) as max_execution_time,
                MIN(min_execution_time) as min_execution_time,
                COUNT(DISTINCT unique_users) as unique_users,
                COUNT(DISTINCT unique_ips) as unique_ips
            FROM data_service_call_stats
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND stats_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND stats_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY service_id, service_name, DATE_FORMAT(stats_time, '%Y-%m')
            ORDER BY stats_month DESC
        </script>
    """)
    fun getServiceCallStatsByMonth(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 清除服务调用统计
     *
     * @param serviceId 服务ID
     * @param before 清除此时间之前的数据
     * @return 清除的记录数
     */
    @Select("""
        <script>
            DELETE FROM data_service_call_stats
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="before != null">
                    AND stats_time &lt; #{before}
                </if>
            </where>
        </script>
    """)
    fun clearServiceCallStats(
        @Param("serviceId") serviceId: String?,
        @Param("before") before: LocalDateTime?
    ): Int
}
