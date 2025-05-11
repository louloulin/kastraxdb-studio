package ai.magicdb.dataservice.core.mapper

import ai.magicdb.dataservice.core.entity.ServiceCallRecordDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import java.time.LocalDateTime

/**
 * 服务调用记录Mapper接口
 *
 * @author magicdb
 */
@Mapper
interface ServiceCallRecordMapper : BaseMapper<ServiceCallRecordDO> {
    
    /**
     * 获取调用记录数量
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param success 是否成功
     * @return 记录数量
     */
    @Select("""
        <script>
            SELECT COUNT(*) FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    service_id = #{serviceId}
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
    fun countRecords(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("success") success: Boolean?
    ): Long
    
    /**
     * 获取平均执行时间
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
                    service_id = #{serviceId}
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
    fun avgExecutionTime(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("success") success: Boolean?
    ): Double?
    
    /**
     * 获取最大执行时间
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
                    service_id = #{serviceId}
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
    fun maxExecutionTime(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("success") success: Boolean?
    ): Long?
    
    /**
     * 获取最小执行时间
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
                    service_id = #{serviceId}
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
    fun minExecutionTime(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("success") success: Boolean?
    ): Long?
    
    /**
     * 获取最后调用时间
     *
     * @param serviceId 服务ID
     * @return 最后调用时间
     */
    @Select("SELECT MAX(call_time) FROM data_service_call_record WHERE service_id = #{serviceId}")
    fun lastCallTime(@Param("serviceId") serviceId: String): LocalDateTime?
    
    /**
     * 获取最后错误时间
     *
     * @param serviceId 服务ID
     * @return 最后错误时间
     */
    @Select("SELECT MAX(call_time) FROM data_service_call_record WHERE service_id = #{serviceId} AND success = 0")
    fun lastErrorTime(@Param("serviceId") serviceId: String): LocalDateTime?
    
    /**
     * 获取唯一用户数
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 唯一用户数
     */
    @Select("""
        <script>
            SELECT COUNT(DISTINCT user_id) FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    service_id = #{serviceId}
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
    fun uniqueUserCount(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): Int
    
    /**
     * 获取唯一IP数
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 唯一IP数
     */
    @Select("""
        <script>
            SELECT COUNT(DISTINCT client_ip) FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    service_id = #{serviceId}
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
    fun uniqueIpCount(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): Int
    
    /**
     * 获取最近调用的服务
     *
     * @param limit 限制数量
     * @return 最近调用的服务ID列表
     */
    @Select("SELECT DISTINCT service_id FROM data_service_call_record ORDER BY MAX(call_time) DESC LIMIT #{limit}")
    fun recentlyCalledServices(@Param("limit") limit: Int): List<String>
    
    /**
     * 获取调用最多的服务
     *
     * @param limit 限制数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 调用最多的服务ID和调用次数
     */
    @Select("""
        <script>
            SELECT service_id, COUNT(*) as call_count
            FROM data_service_call_record
            <where>
                <if test="startTime != null">
                    call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY service_id
            ORDER BY call_count DESC
            LIMIT #{limit}
        </script>
    """)
    fun mostCalledServices(
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
     * @return 错误最多的服务ID和错误次数
     */
    @Select("""
        <script>
            SELECT service_id, COUNT(*) as error_count
            FROM data_service_call_record
            <where>
                success = 0
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY service_id
            ORDER BY error_count DESC
            LIMIT #{limit}
        </script>
    """)
    fun mostErrorServices(
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
     * @return 性能最差的服务ID和平均执行时间
     */
    @Select("""
        <script>
            SELECT service_id, AVG(execution_time) as avg_time
            FROM data_service_call_record
            <where>
                success = 1
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY service_id
            HAVING COUNT(*) > 0
            ORDER BY avg_time DESC
            LIMIT #{limit}
        </script>
    """)
    fun worstPerformanceServices(
        @Param("limit") limit: Int,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 获取错误类型统计
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 错误类型和次数
     */
    @Select("""
        <script>
            SELECT error_type, COUNT(*) as error_count
            FROM data_service_call_record
            <where>
                success = 0
                AND error_type IS NOT NULL
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
            GROUP BY error_type
            ORDER BY error_count DESC
        </script>
    """)
    fun errorTypeStats(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?
    ): List<Map<String, Any>>
    
    /**
     * 获取最常见的错误消息
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 错误消息和次数
     */
    @Select("""
        <script>
            SELECT error_message, COUNT(*) as error_count
            FROM data_service_call_record
            <where>
                success = 0
                AND error_message IS NOT NULL
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
            GROUP BY error_message
            ORDER BY error_count DESC
            LIMIT #{limit}
        </script>
    """)
    fun mostCommonErrors(
        @Param("serviceId") serviceId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        @Param("limit") limit: Int
    ): List<Map<String, Any>>
    
    /**
     * 获取按小时统计的调用次数
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 小时和调用次数
     */
    @Select("""
        <script>
            SELECT HOUR(call_time) as hour, COUNT(*) as call_count
            FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY HOUR(call_time)
            ORDER BY hour
        </script>
    """)
    fun callsByHour(
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
     * @return 日期和调用次数
     */
    @Select("""
        <script>
            SELECT DATE_FORMAT(call_time, '%Y-%m-%d') as day, COUNT(*) as call_count
            FROM data_service_call_record
            <where>
                <if test="serviceId != null">
                    service_id = #{serviceId}
                </if>
                <if test="startTime != null">
                    AND call_time >= #{startTime}
                </if>
                <if test="endTime != null">
                    AND call_time &lt;= #{endTime}
                </if>
            </where>
            GROUP BY DATE_FORMAT(call_time, '%Y-%m-%d')
            ORDER BY day
        </script>
    """)
    fun callsByDay(
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
     * @return 小时和错误次数
     */
    @Select("""
        <script>
            SELECT HOUR(call_time) as hour, COUNT(*) as error_count
            FROM data_service_call_record
            <where>
                success = 0
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
            ORDER BY hour
        </script>
    """)
    fun errorsByHour(
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
     * @return 日期和错误次数
     */
    @Select("""
        <script>
            SELECT DATE_FORMAT(call_time, '%Y-%m-%d') as day, COUNT(*) as error_count
            FROM data_service_call_record
            <where>
                success = 0
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
            ORDER BY day
        </script>
    """)
    fun errorsByDay(
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
                    service_id = #{serviceId}
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
