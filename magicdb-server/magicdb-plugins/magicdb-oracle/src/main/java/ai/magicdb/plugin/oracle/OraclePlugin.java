package ai.magicdb.plugin.oracle;


import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class OraclePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"oracle.json", DBConfig.class);

    }

    @Override
    public MetaData getMetaData() {
        return new OracleMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new OracleDBManage();
    }
}
