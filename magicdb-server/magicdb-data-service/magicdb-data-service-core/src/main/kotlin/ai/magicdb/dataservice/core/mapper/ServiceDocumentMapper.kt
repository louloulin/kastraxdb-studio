package ai.magicdb.dataservice.core.mapper

import ai.magicdb.dataservice.core.entity.ServiceDocumentDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

/**
 * 服务文档Mapper接口
 *
 * @author magicdb
 */
@Mapper
interface ServiceDocumentMapper : BaseMapper<ServiceDocumentDO> {
    
    /**
     * 根据服务ID查询文档
     *
     * @param serviceId 服务ID
     * @return 文档
     */
    @Select("SELECT * FROM data_service_document WHERE service_id = #{serviceId} ORDER BY sort ASC, create_time DESC LIMIT 1")
    fun selectByServiceId(@Param("serviceId") serviceId: String): ServiceDocumentDO?
    
    /**
     * 根据分组ID查询文档
     *
     * @param groupId 分组ID
     * @return 文档
     */
    @Select("SELECT * FROM data_service_document WHERE group_id = #{groupId} ORDER BY sort ASC, create_time DESC LIMIT 1")
    fun selectByGroupId(@Param("groupId") groupId: String): ServiceDocumentDO?
    
    /**
     * 查询API文档
     *
     * @return 文档
     */
    @Select("SELECT * FROM data_service_document WHERE service_id IS NULL AND group_id IS NULL ORDER BY sort ASC, create_time DESC LIMIT 1")
    fun selectApiDocument(): ServiceDocumentDO?
}
