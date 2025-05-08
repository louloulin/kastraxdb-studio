package ai.magicdb.plugin.timeplus;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class TimeplusPlugin implements Plugin {

    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(
            this.getClass(),
            "timeplus.json",
            DBConfig.class
        );
    }

    @Override
    public MetaData getMetaData() {
        return new TimeplusMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new TimeplusDBManage();
    }
}
