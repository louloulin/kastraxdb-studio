package ai.magicdb.dataservice.core.mapper

import ai.magicdb.dataservice.core.entity.ServiceHistoryDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

/**
 * 服务历史记录Mapper接口
 *
 * @author magicdb
 */
interface ServiceHistoryMapper : BaseMapper<ServiceHistoryDO> {

    /**
     * 根据服务ID查询最新版本号
     *
     * @param serviceId 服务ID
     * @return 最新版本号
     */
    @Select("SELECT MAX(VERSION) FROM DS_SERVICE_HISTORY WHERE SERVICE_ID = #{serviceId}")
    fun selectMaxVersionByServiceId(@Param("serviceId") serviceId: String): Int?
}
