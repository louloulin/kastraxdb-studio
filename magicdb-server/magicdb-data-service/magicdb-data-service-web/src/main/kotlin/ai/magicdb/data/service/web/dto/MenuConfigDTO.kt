package ai.magicdb.data.service.web.dto

/**
 * DTO for menu configuration
 */
data class MenuConfigDTO(
    val id: String,
    val name: String,
    val icon: String? = null,
    val path: String? = null,
    val url: String? = null,
    val order: Int = 0,
    val children: List<MenuConfigDTO>? = null
)
