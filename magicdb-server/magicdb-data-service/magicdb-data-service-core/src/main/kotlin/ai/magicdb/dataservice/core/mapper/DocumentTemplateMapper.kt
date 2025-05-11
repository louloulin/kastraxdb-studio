package ai.magicdb.dataservice.core.mapper

import ai.magicdb.dataservice.core.entity.DocumentTemplateDO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

/**
 * 文档模板Mapper接口
 *
 * @author magicdb
 */
@Mapper
interface DocumentTemplateMapper : BaseMapper<DocumentTemplateDO> {
    
    /**
     * 根据类型查询默认模板
     *
     * @param type 模板类型
     * @return 默认模板
     */
    @Select("SELECT * FROM data_service_document_template WHERE type = #{type} AND is_system = 1 ORDER BY sort ASC, create_time DESC LIMIT 1")
    fun selectDefaultByType(@Param("type") type: String): DocumentTemplateDO?
    
    /**
     * 根据类型查询所有模板
     *
     * @param type 模板类型
     * @return 模板列表
     */
    @Select("SELECT * FROM data_service_document_template WHERE type = #{type} ORDER BY sort ASC, create_time DESC")
    fun selectByType(@Param("type") type: String): List<DocumentTemplateDO>
}
