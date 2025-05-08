package ai.magicdb.plugin.clickhouse;


import ai.magicdb.spi.DBManage;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.Plugin;
import ai.magicdb.spi.config.DBConfig;
import ai.magicdb.spi.util.FileUtils;

public class ClickHousePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"clickhouse.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new ClickHouseMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new ClickHouseDBManage();
    }
}
