package ai.magicdb.plugin.dm;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;
import org.springframework.context.annotation.ComponentScan;

/**
 * DM 数据库插件
 * 支持达梦数据库的连接和操作
 */
@ComponentScan("ai.magicdb.plugin.dm.controller")
public class DMPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"dm.json", DBConfig.class);

    }

    @Override
    public MetaData getMetaData() {
        return new DMMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new DMDBManage();
    }
}
