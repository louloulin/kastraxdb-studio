package ai.magicdb.plugin.hive;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class HivePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"hive.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new HiveMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new HiveDBManage();
    }
}
