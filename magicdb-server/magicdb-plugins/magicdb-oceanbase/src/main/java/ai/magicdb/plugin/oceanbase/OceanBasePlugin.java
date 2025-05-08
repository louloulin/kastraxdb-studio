package ai.magicdb.plugin.oceanbase;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class OceanBasePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"oceanbase.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new OceanBaseMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new OceanBaseDBManage();
    }
}
