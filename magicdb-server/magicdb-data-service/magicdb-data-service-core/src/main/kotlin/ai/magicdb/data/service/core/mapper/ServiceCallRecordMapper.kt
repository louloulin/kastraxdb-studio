package ai.magicdb.data.service.core.mapper

import ai.magicdb.data.service.core.entity.ServiceCallRecordDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import java.time.LocalDateTime

/**
 * 服务调用记录Mapper
 *
 * @author magicdb
 */
@Mapper
interface ServiceCallRecordMapper : BaseMapper<ServiceCallRecordDO> {
    
    /**
     * 获取服务调用记录数量
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 是否成功
     * @return 调用记录数量
     */
    @Select("""
        <script>
            SELECT COUNT(*) FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                <if test="success != null">
                    AND success = #{success}
                </if>
            </where>
        </script>
    """)
    fun getCallRecordCount(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("success") success: Boolean?
    ): Long
    
    /**
     * 获取服务平均执行时间
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 是否成功
     * @return 平均执行时间
     */
    @Select("""
        <script>
            SELECT AVG(execution_time) FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                <if test="success != null">
                    AND success = #{success}
                </if>
            </where>
        </script>
    """)
    fun getAvgExecutionTime(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("success") success: Boolean?
    ): Double
    
    /**
     * 获取服务最大执行时间
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 是否成功
     * @return 最大执行时间
     */
    @Select("""
        <script>
            SELECT MAX(execution_time) FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                <if test="success != null">
                    AND success = #{success}
                </if>
            </where>
        </script>
    """)
    fun getMaxExecutionTime(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("success") success: Boolean?
    ): Long
    
    /**
     * 获取服务最小执行时间
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 是否成功
     * @return 最小执行时间
     */
    @Select("""
        <script>
            SELECT MIN(execution_time) FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                <if test="success != null">
                    AND success = #{success}
                </if>
            </where>
        </script>
    """)
    fun getMinExecutionTime(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("success") success: Boolean?
    ): Long
    
    /**
     * 获取服务最后调用时间
     *
     * @param serviceId 服务ID
     * @return 最后调用时间
     */
    @Select("""
        <script>
            SELECT MAX(call_time) FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
            </where>
        </script>
    """)
    fun getLastCallTime(@Param("serviceId") serviceId: String?): LocalDateTime?
    
    /**
     * 获取服务最后错误时间
     *
     * @param serviceId 服务ID
     * @return 最后错误时间
     */
    @Select("""
        SELECT MAX(call_time) FROM data_service_call_record
        WHERE service_id = #{serviceId} AND success = 0
    """)
    fun getLastErrorTime(@Param("serviceId") serviceId: String): LocalDateTime?
    
    /**
     * 获取服务调用用户数
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 调用用户数
     */
    @Select("""
        <script>
            SELECT COUNT(DISTINCT user_id) FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                AND user_id IS NOT NULL
            </where>
        </script>
    """)
    fun getUniqueUserCount(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): Int
    
    /**
     * 获取服务调用IP数
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 调用IP数
     */
    @Select("""
        <script>
            SELECT COUNT(DISTINCT client_ip) FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                AND client_ip IS NOT NULL
            </where>
        </script>
    """)
    fun getUniqueIpCount(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): Int
    
    /**
     * 获取服务错误类型统计
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 错误类型统计
     */
    @Select("""
        <script>
            SELECT error_type, COUNT(*) as count FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                AND success = 0
                AND error_type IS NOT NULL
            </where>
            GROUP BY error_type
        </script>
    """)
    fun getErrorTypeStats(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): Map<String, Long>
    
    /**
     * 获取服务最常见的错误消息
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 最常见的错误消息
     */
    @Select("""
        <script>
            SELECT error_message, COUNT(*) as count FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                AND success = 0
                AND error_message IS NOT NULL
            </where>
            GROUP BY error_message
            ORDER BY count DESC
            LIMIT #{limit}
        </script>
    """)
    fun getMostCommonErrors(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("limit") limit: Int
    ): List<Map<String, Any>>
    
    /**
     * 获取最近调用的服务
     *
     * @param limit 限制数量
     * @return 最近调用的服务
     */
    @Select("""
        SELECT service_id FROM (
            SELECT service_id, MAX(call_time) as last_call_time
            FROM data_service_call_record
            GROUP BY service_id
        ) t
        ORDER BY last_call_time DESC
        LIMIT #{limit}
    """)
    fun getRecentlyCalledServices(@Param("limit") limit: Int): List<String>
    
    /**
     * 获取调用最多的服务
     *
     * @param limit 限制数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 调用最多的服务
     */
    @Select("""
        <script>
            SELECT service_id, COUNT(*) as count FROM data_service_call_record
            <where>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY service_id
            ORDER BY count DESC
            LIMIT #{limit}
        </script>
    """)
    fun getMostCalledServices(
        @Param("limit") limit: Int,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 获取错误最多的服务
     *
     * @param limit 限制数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 错误最多的服务
     */
    @Select("""
        <script>
            SELECT service_id, COUNT(*) as count FROM data_service_call_record
            <where>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                AND success = 0
            </where>
            GROUP BY service_id
            ORDER BY count DESC
            LIMIT #{limit}
        </script>
    """)
    fun getMostErrorServices(
        @Param("limit") limit: Int,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 获取性能最差的服务
     *
     * @param limit 限制数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 性能最差的服务
     */
    @Select("""
        <script>
            SELECT service_id, AVG(execution_time) as avg_time FROM data_service_call_record
            <where>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                AND success = 1
            </where>
            GROUP BY service_id
            ORDER BY avg_time DESC
            LIMIT #{limit}
        </script>
    """)
    fun getWorstPerformanceServices(
        @Param("limit") limit: Int,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 获取按小时统计的调用次数
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 按小时统计的调用次数
     */
    @Select("""
        <script>
            SELECT HOUR(call_time) as hour, COUNT(*) as count FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY HOUR(call_time)
        </script>
    """)
    fun getCallsByHour(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 获取按天统计的调用次数
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 按天统计的调用次数
     */
    @Select("""
        <script>
            SELECT DATE_FORMAT(call_time, '%Y-%m-%d') as day, COUNT(*) as count FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY DATE_FORMAT(call_time, '%Y-%m-%d')
        </script>
    """)
    fun getCallsByDay(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 获取按小时统计的错误次数
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 按小时统计的错误次数
     */
    @Select("""
        <script>
            SELECT HOUR(call_time) as hour, COUNT(*) as count FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                AND success = 0
            </where>
            GROUP BY HOUR(call_time)
        </script>
    """)
    fun getErrorsByHour(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 获取按天统计的错误次数
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 按天统计的错误次数
     */
    @Select("""
        <script>
            SELECT DATE_FORMAT(call_time, '%Y-%m-%d') as day, COUNT(*) as count FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
                AND success = 0
            </where>
            GROUP BY DATE_FORMAT(call_time, '%Y-%m-%d')
        </script>
    """)
    fun getErrorsByDay(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 清除监控数据
     *
     * @param serviceId 服务ID
     * @param before 清除此时间之前的数据
     * @return 清除的记录数
     */
    @Select("""
        <script>
            DELETE FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    AND service_id = #{serviceId}
                </if>
                <if test="before != null">
                    AND call_time &lt; #{before}
                </if>
            </where>
        </script>
    """)
    fun clearMonitoringData(
        @Param("serviceId") serviceId: String?,
        @Param("before") before: LocalDateTime?
    ): Int
}
