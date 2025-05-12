package ai.magicdb.data.service.core.mapper

import ai.magicdb.data.service.core.entity.ServiceTestDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

/**
 * 服务测试Mapper接口
 *
 * @author magicdb
 */
@Mapper
interface ServiceTestMapper : BaseMapper<ServiceTestDO> {
    
    /**
     * 根据服务ID查询测试列表
     *
     * @param serviceId 服务ID
     * @return 测试列表
     */
    @Select("SELECT * FROM data_service_test WHERE service_id = #{serviceId} ORDER BY sort ASC, create_time DESC")
    fun selectByServiceId(@Param("serviceId") serviceId: String): List<ServiceTestDO>
}
