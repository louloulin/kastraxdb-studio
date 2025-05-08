package ai.magicdb.plugin.presto;

import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.jdbc.DefaultMetaService;

public class PrestoMetaData extends DefaultMetaService implements MetaData {
    public String tableDDL(String databaseName, String schemaName,String tableName) {
        return "";
    }
}
