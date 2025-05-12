package ai.magicdb.data.service.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * 文档模板实体类
 *
 * @author magicdb
 */
@TableName("data_service_document_template")
class DocumentTemplateDO {
    
    /**
     * 模板ID
     */
    @TableId(type = IdType.INPUT)
    var id: String = ""
    
    /**
     * 模板名称
     */
    var name: String = ""
    
    /**
     * 模板内容
     */
    var content: String = ""
    
    /**
     * 模板格式（如：markdown, html）
     */
    var format: String = "markdown"
    
    /**
     * 模板类型（如：service, group, api）
     */
    var type: String = "service"
    
    /**
     * 创建时间
     */
    var createTime: Long? = null
    
    /**
     * 更新时间
     */
    var updateTime: Long? = null
    
    /**
     * 创建用户ID
     */
    var createUserId: Long? = null
    
    /**
     * 是否系统默认
     */
    var isSystem: Boolean? = false
    
    /**
     * 是否启用
     */
    var enabled: Boolean? = true
    
    /**
     * 排序
     */
    var sort: Int? = 0
    
    /**
     * 描述
     */
    var description: String? = ""
}
