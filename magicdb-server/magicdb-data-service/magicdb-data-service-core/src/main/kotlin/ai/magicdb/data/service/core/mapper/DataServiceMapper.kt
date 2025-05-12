package ai.magicdb.data.service.core.mapper

import ai.magicdb.data.service.core.entity.DataServiceDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

/**
 * 数据服务Mapper接口
 *
 * @author magicdb
 */
interface DataServiceMapper : BaseMapper<DataServiceDO> {

    /**
     * 根据标签查询服务列表
     *
     * @param tag 标签
     * @return 服务列表
     */
    @Select("SELECT ds.* FROM DS_DATA_SERVICE ds JOIN DS_SERVICE_TAG st ON ds.ID = st.SERVICE_ID WHERE st.NAME = #{tag}")
    fun selectByTag(@Param("tag") tag: String): List<DataServiceDO>
}
