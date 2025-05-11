package ai.magicdb.dataservice.core.mapper

import ai.magicdb.dataservice.core.entity.ServiceCallStatsDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import java.time.LocalDateTime

/**
 * 服务调用统计Mapper接口
 *
 * @author magicdb
 */
@Mapper
interface ServiceCallStatsMapper : BaseMapper<ServiceCallStatsDO> {
    
    /**
     * 获取服务统计信息
     *
     * @param serviceId 服务ID
     * @param statType 统计类型
     * @param statPeriod 统计周期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    @Select("""
        SELECT * FROM data_service_call_stats
        WHERE service_id = #{serviceId}
        AND stat_type = #{statType}
        AND stat_period = #{statPeriod}
        AND start_time = #{startTime}
        AND end_time = #{endTime}
        LIMIT 1
    """)
    fun getServiceStats(
        @Param("serviceId") serviceId: String,
        @Param("statType") statType: String,
        @Param("statPeriod") statPeriod: String,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): ServiceCallStatsDO?
    
    /**
     * 清除过期统计数据
     *
     * @param before 清除此时间之前的数据
     * @return 清除的记录数
     */
    @Select("DELETE FROM data_service_call_stats WHERE update_time < #{before}")
    fun clearExpiredStats(@Param("before") before: LocalDateTime): Int
}
