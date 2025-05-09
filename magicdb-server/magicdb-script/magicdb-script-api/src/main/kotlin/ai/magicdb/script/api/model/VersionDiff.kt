package ai.magicdb.script.api

import java.io.Serializable

/**
 * 版本差异
 *
 * @author magicdb
 */
data class VersionDiff(
    /**
     * 脚本ID
     */
    val scriptId: String,

    /**
     * 起始版本
     */
    val fromVersion: Int,

    /**
     * 目标版本
     */
    val toVersion: Int,

    /**
     * 差异行
     */
    val diffLines: List<DiffLine>
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 差异行
 */
data class DiffLine(
    /**
     * 行号
     */
    val lineNumber: Int,

    /**
     * 操作类型
     */
    val type: DiffType,

    /**
     * 原始内容
     */
    val oldContent: String?,

    /**
     * 新内容
     */
    val newContent: String?
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * 差异类型
 */
enum class DiffType {
    /**
     * 添加
     */
    ADD,

    /**
     * 删除
     */
    DELETE,

    /**
     * 修改
     */
    MODIFY,

    /**
     * 不变
     */
    UNCHANGED
}
