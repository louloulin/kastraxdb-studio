package ai.magicdb.data.service.core.document

import ai.magicdb.data.service.api.model.ServiceDocumentExample
import ai.magicdb.data.service.api.model.FieldDocument
import ai.magicdb.data.service.api.model.ParameterDocument
import org.springframework.stereotype.Component

/**
 * 示例生成器
 * 用于生成示例请求和响应
 *
 * @author magicdb
 */
@Component
class ExampleGenerator {

    /**
     * 生成参数示例
     *
     * @param parameters 参数列表
     * @return 参数示例 JSON 字符串
     */
    fun generateParameterExample(parameters: List<ParameterDocument>): String {
        // 实现生成参数示例的逻辑
        return "{}"
    }

    /**
     * 生成返回值示例
     *
     * @param fields 字段列表
     * @return 返回值示例 JSON 字符串
     */
    fun generateReturnExample(fields: List<FieldDocument>): String {
        // 实现生成返回值示例的逻辑
        return "{}"
    }

    /**
     * 生成文档示例
     *
     * @param parameters 参数列表
     * @param returnFields 返回字段列表
     * @return 文档示例
     */
    fun generateDocumentExample(parameters: List<ParameterDocument>, returnFields: List<FieldDocument>): ServiceDocumentExample {
        // 实现生成文档示例的逻辑
        return ServiceDocumentExample(
            name = "示例",
            request = generateParameterExample(parameters),
            response = generateReturnExample(returnFields),
            description = "示例请求和响应"
        )
    }
}
