package ai.magicdb.plugin.mysql;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class MysqlPlugin implements Plugin {

    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"mysql.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new MysqlMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new MysqlDBManage();
    }
}
