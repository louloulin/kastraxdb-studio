package ai.magicdb.script.runtime

import ai.magicdb.spi.DBManage
import ai.magicdb.spi.MetaData
import ai.magicdb.spi.Plugin
import ai.magicdb.spi.config.DBConfig
import ai.magicdb.spi.config.DriverConfig
import org.slf4j.LoggerFactory

/**
 * 脚本插件
 *
 * @author magicdb
 */
class ScriptPlugin : Plugin {
    private val logger = LoggerFactory.getLogger(ScriptPlugin::class.java)
    private val dbConfig: DBConfig
    private val metaData: ScriptMetaData
    private val dbManage: ScriptDBManage

    init {
        this.dbConfig = createDBConfig()
        this.metaData = ScriptMetaData()
        this.dbManage = ScriptDBManage()
    }

    override fun getDBConfig(): DBConfig {
        return dbConfig
    }

    override fun getMetaData(): MetaData {
        return metaData
    }

    override fun getDBManage(): DBManage {
        return dbManage
    }

    /**
     * 创建数据库配置
     *
     * @return 数据库配置
     */
    private fun createDBConfig(): DBConfig {
        val config = DBConfig()
        config.dbType = "SCRIPT"
        config.name = "Script"
        config.isSupportDatabase = true
        config.isSupportSchema = true
        
        val driverConfigs = ArrayList<DriverConfig>()
        val driverConfig = DriverConfig()
        driverConfig.url = "script://localhost"
        driverConfig.isCustom = false
        driverConfig.isDefaultDriver = true
        driverConfig.jdbcDriverClass = "ai.magicdb.script.runtime.ScriptDriver"
        driverConfigs.add(driverConfig)
        
        config.driverConfigList = driverConfigs
        
        return config
    }
}
