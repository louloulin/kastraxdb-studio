package ai.magicdb.plugin.mongodb;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class MongodbPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"mongodb.json", DBConfig.class);

    }

    @Override
    public MetaData getMetaData() {
        return new MongodbMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new MongodbManage();
    }
}
