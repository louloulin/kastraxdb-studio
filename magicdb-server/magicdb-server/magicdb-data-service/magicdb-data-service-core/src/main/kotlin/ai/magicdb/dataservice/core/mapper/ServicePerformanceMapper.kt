package ai.magicdb.dataservice.core.mapper

import ai.magicdb.dataservice.core.entity.ServicePerformanceDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import java.time.LocalDateTime

/**
 * 服务性能监控Mapper接口
 *
 * @author magicdb
 */
@Mapper
interface ServicePerformanceMapper : BaseMapper<ServicePerformanceDO> {
    
    /**
     * 获取服务平均性能指标
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均性能指标
     */
    @Select("""
        <script>
            SELECT 
                AVG(memory_usage) as memory_usage,
                AVG(cpu_usage) as cpu_usage,
                AVG(thread_count) as thread_count,
                AVG(active_connections) as active_connections,
                AVG(requests_per_second) as requests_per_second,
                AVG(avg_response_time) as avg_response_time
            FROM data_service_performance
            <where>
                <if test="serviceId != null">
                    service_id = #{serviceId}
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
    fun getAveragePerformance(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): Map<String, Any>?
    
    /**
     * 清除性能监控数据
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
                    service_id = #{serviceId}
                </if>
                <if test="before != null">
                    AND record_time &lt; #{before}
                </if>
            </where>
        </script>
    """)
    fun clearPerformanceData(
        @Param("serviceId") serviceId: String?,
        @Param("before") before: LocalDateTime?
    ): Int
}
