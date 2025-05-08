package ai.magicdb.plugin.mariadb;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class MariaDBPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"mariadb.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new MariaDBMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new MariaDBManage();
    }
}
