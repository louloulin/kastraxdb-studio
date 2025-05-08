package ai.magicdb.plugin.sqlite;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class SqlitePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"sqlite.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new SqliteMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new SqliteDBManage();
    }
}
