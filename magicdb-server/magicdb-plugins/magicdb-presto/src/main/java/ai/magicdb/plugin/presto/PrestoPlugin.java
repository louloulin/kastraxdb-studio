package ai.magicdb.plugin.presto;


import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class PrestoPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"presto.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new PrestoMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new PrestoDBManage();
    }
}
