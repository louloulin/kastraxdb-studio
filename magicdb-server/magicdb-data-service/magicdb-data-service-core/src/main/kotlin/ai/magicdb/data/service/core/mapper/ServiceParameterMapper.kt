package ai.magicdb.data.service.core.mapper

import ai.magicdb.data.service.core.entity.ServiceParameterDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Param

/**
 * 服务参数Mapper接口
 *
 * @author magicdb
 */
interface ServiceParameterMapper : BaseMapper<ServiceParameterDO> {

    /**
     * 根据服务ID删除参数
     *
     * @param serviceId 服务ID
     * @return 影响行数
     */
    @Delete("DELETE FROM DS_SERVICE_PARAMETER WHERE SERVICE_ID = #{serviceId}")
    fun deleteByServiceId(@Param("serviceId") serviceId: String): Int
}
