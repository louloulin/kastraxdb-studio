package ai.magicdb.plugin.db2;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class DB2Plugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"db2.json", DBConfig.class);

    }

    @Override
    public MetaData getMetaData() {
        return new DB2MetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new DB2DBManage();
    }
}
