package ai.magicdb.data.service.core.mapper

import ai.magicdb.data.service.core.entity.ServicePerformanceDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import java.time.LocalDateTime

/**
 * 服务性能Mapper
 *
 * @author magicdb
 */
@Mapper
interface ServicePerformanceMapper : BaseMapper<ServicePerformanceDO> {
    
    /**
     * 获取服务性能记录
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 服务性能记录
     */
    @Select("""
        <script>
            SELECT * FROM data_service_performance
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND record_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND record_time &lt;= #{endTime}
                </if>
            </where>
            ORDER BY record_time DESC
            LIMIT #{limit} OFFSET #{offset}
        </script>
    """)
    fun getServicePerformanceRecords(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("limit") limit: Int,
        @Param("offset") offset: Int
    ): List<ServicePerformanceDO>
    
    /**
     * 获取服务性能记录数量
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务性能记录数量
     */
    @Select("""
        <script>
            SELECT COUNT(*) FROM data_service_performance
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND record_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND record_time &lt;= #{endTime}
                </if>
            </where>
        </script>
    """)
    fun getServicePerformanceRecordCount(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): Long
    
    /**
     * 获取服务平均性能指标
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务平均性能指标
     */
    @Select("""
        <script>
            SELECT 
                service_id,
                service_name,
                AVG(memory_usage) as avg_memory_usage,
                AVG(cpu_usage) as avg_cpu_usage,
                AVG(thread_count) as avg_thread_count,
                AVG(active_connections) as avg_active_connections,
                AVG(requests_per_second) as avg_requests_per_second,
                AVG(avg_response_time) as avg_response_time
            FROM data_service_performance
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND record_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND record_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY service_id, service_name
        </script>
    """)
    fun getServiceAvgPerformanceMetrics(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 获取服务性能趋势
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param interval 时间间隔（分钟）
     * @return 服务性能趋势
     */
    @Select("""
        <script>
            SELECT 
                service_id,
                service_name,
                DATE_FORMAT(record_time, '%Y-%m-%d %H:%i:00') as time_slot,
                AVG(memory_usage) as avg_memory_usage,
                AVG(cpu_usage) as avg_cpu_usage,
                AVG(thread_count) as avg_thread_count,
                AVG(active_connections) as avg_active_connections,
                AVG(requests_per_second) as avg_requests_per_second,
                AVG(avg_response_time) as avg_response_time
            FROM data_service_performance
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND record_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND record_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY service_id, service_name, DATE_FORMAT(record_time, '%Y-%m-%d %H:%i:00')
            ORDER BY time_slot
        </script>
    """)
    fun getServicePerformanceTrend(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("interval") interval: Int
    ): List<Map<String, Any>>
    
    /**
     * 清除服务性能记录
     *
     * @param serviceId 服务ID
     * @param before 清除此时间之前的数据
     * @return 清除的记录数
     */
    @Select("""
        <script>
            DELETE FROM data_service_performance
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="before != null">
                    AND record_time &lt; #{before}
                </if>
            </where>
        </script>
    """)
    fun clearServicePerformanceRecords(
        @Param("serviceId") serviceId: String?,
        @Param("before") before: LocalDateTime?
    ): Int
}
