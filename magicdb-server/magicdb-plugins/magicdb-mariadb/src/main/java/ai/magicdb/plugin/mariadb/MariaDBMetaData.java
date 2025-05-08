package ai.magicdb.plugin.mariadb;


import ai.magicdb.plugin.mariadb.value.MariaDBValueProcessor;
import ai.magicdb.plugin.mysql.MysqlMetaData;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.ValueProcessor;

public class MariaDBMetaData extends MysqlMetaData implements MetaData {

    @Override
    public ValueProcessor getValueProcessor() {
        return new MariaDBValueProcessor();
    }
}
