package ai.magicdb.plugin.kingbase;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class KingBasePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"kingbase.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new KingBaseMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new KingBaseDBManage();
    }
}
