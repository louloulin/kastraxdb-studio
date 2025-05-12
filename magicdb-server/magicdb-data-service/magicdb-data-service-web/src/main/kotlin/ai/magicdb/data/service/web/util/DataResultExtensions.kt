package ai.magicdb.data.service.web.util

import ai.magicdb.server.tools.base.wrapper.result.DataResult

/**
 * Extensions for DataResult class
 */
object DataResultExtensions {
    
    /**
     * Create a failed DataResult with a message
     */
    fun <T> failed(message: String): DataResult<T> {
        val result = DataResult<T>()
        result.success = false
        result.errorMessage = message
        return result
    }
    
    /**
     * Create a DataResult with data, message, and success flag
     */
    fun <T> of(data: T?, message: String, success: Boolean): DataResult<T> {
        val result = DataResult<T>()
        result.data = data
        result.success = success
        result.errorMessage = message
        return result
    }
}
