package ai.magicdb.plugin.postgresql;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class PostgreSQLPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"pg.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new PostgreSQLMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new PostgreSQLDBManage();
    }
}
