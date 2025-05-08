package ai.magicdb.plugin.dm;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class DMPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"dm.json", DBConfig.class);

    }

    @Override
    public MetaData getMetaData() {
        return new DMMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new DMDBManage();
    }
}
