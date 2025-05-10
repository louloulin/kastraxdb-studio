package ai.magicdb.dataservice.core.mapper

import ai.magicdb.dataservice.core.entity.ServiceTagDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Param

/**
 * 服务标签Mapper接口
 *
 * @author magicdb
 */
interface ServiceTagMapper : BaseMapper<ServiceTagDO> {

    /**
     * 根据服务ID删除标签
     *
     * @param serviceId 服务ID
     * @return 影响行数
     */
    @Delete("DELETE FROM DS_SERVICE_TAG WHERE SERVICE_ID = #{serviceId}")
    fun deleteByServiceId(@Param("serviceId") serviceId: String): Int
}
