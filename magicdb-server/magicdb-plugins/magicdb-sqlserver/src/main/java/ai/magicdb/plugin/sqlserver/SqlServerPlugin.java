package ai.magicdb.plugin.sqlserver;


import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class SqlServerPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"sqlserver.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new SqlServerMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new SqlServerDBManage();
    }
}
