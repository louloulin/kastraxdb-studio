package ai.magicdb.data.service.core.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * 服务测试实体类
 *
 * @author magicdb
 */
@TableName("data_service_test")
class ServiceTestDO {
    
    /**
     * 测试ID
     */
    @TableId(type = IdType.INPUT)
    var id: String = ""
    
    /**
     * 测试名称
     */
    var name: String = ""
    
    /**
     * 服务ID
     */
    var serviceId: String = ""
    
    /**
     * 测试参数（JSON格式）
     */
    var parameters: String? = null
    
    /**
     * 预期结果（JSON格式）
     */
    var expectedResult: String? = null
    
    /**
     * 测试描述
     */
    var description: String? = null
    
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
     * 是否启用
     */
    var enabled: Boolean? = null
    
    /**
     * 排序
     */
    var sort: Int? = null
}
