package ai.magicdb.data.service.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * 服务文档实体类
 *
 * @author magicdb
 */
@TableName("data_service_document")
class ServiceDocumentDO {
    
    /**
     * 文档ID
     */
    @TableId(type = IdType.INPUT)
    var id: String = ""
    
    /**
     * 文档标题
     */
    var title: String = ""
    
    /**
     * 文档内容
     */
    var content: String = ""
    
    /**
     * 文档格式（如：markdown, html）
     */
    var format: String = "markdown"
    
    /**
     * 服务ID
     */
    var serviceId: String? = null
    
    /**
     * 分组ID
     */
    var groupId: String? = null
    
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
     * 标签（JSON格式）
     */
    var tags: String? = null
    
    /**
     * 是否公开
     */
    var isPublic: Boolean? = true
    
    /**
     * 排序
     */
    var sort: Int? = 0
    
    /**
     * 元数据（JSON格式）
     */
    var metadata: String? = null
}
