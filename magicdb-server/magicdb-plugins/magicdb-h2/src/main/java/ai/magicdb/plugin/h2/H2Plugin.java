package ai.magicdb.plugin.h2;

import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.jdbc.DefaultMetaService;
import ai.magicdb.spi.util.FileUtils;

public class H2Plugin extends DefaultMetaService implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"h2.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new H2Meta();
    }

    @Override
    public DBManage getDBManage() {
        return new H2DBManage();
    }

}
