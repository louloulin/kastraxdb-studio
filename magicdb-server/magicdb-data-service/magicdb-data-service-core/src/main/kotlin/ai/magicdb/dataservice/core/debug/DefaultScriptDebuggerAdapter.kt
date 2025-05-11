package ai.magicdb.dataservice.core.debug

import ai.magicdb.dataservice.api.ScriptDebugger
import ai.magicdb.dataservice.api.model.ScriptDebugRequest
import ai.magicdb.dataservice.api.model.ScriptDebugResult
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

/**
 * 默认脚本调试器适配器
 *
 * @author magicdb
 */
@Service
class DefaultScriptDebuggerAdapter : ScriptDebugger {
    private val logger = LoggerFactory.getLogger(DefaultScriptDebuggerAdapter::class.java)
    
    @Autowired
    private lateinit var delegate: ai.magicdb.dataservice.core.debug.DefaultScriptDebugger
    
    // 会话缓存
    private val sessions = ConcurrentHashMap<String, ScriptDebugRequest>()

    override fun debug(request: ScriptDebugRequest): ScriptDebugResult {
        return delegate.debug(request)
    }

    override fun getLanguages(): List<String> {
        return delegate.getLanguages()
    }

    override fun getTemplate(language: String): String {
        return delegate.getTemplate(language)
    }
    
    override fun createSession(request: ScriptDebugRequest): String {
        return delegate.createSession(request)
    }
    
    override fun closeSession(sessionId: String): Boolean {
        return delegate.closeSession(sessionId)
    }
    
    override fun getSession(sessionId: String): Map<String, Any?> {
        return delegate.getSession(sessionId)
    }
    
    override fun executeCommand(sessionId: String, command: String): ScriptDebugResult {
        return delegate.executeCommand(sessionId, command)
    }
}
