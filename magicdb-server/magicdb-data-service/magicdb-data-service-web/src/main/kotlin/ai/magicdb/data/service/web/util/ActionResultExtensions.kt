package ai.magicdb.data.service.web.util

import ai.magicdb.server.tools.base.wrapper.result.ActionResult

/**
 * Extensions for ActionResult class
 */
object ActionResultExtensions {
    
    /**
     * Create a failed ActionResult with a message
     */
    fun isFailed(message: String): ActionResult {
        val result = ActionResult()
        result.success = false
        result.errorMessage = message
        return result
    }
}
