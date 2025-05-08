package ai.magicdb.plugin.mongodb;

import ai.magicdb.spi.CommandExecutor;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.jdbc.DefaultMetaService;
import ai.magicdb.spi.model.Database;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.util.List;



public class MongodbMetaData extends DefaultMetaService implements MetaData {

    @Override
    public List<Database> databases(Connection connection) {
        return Lists.newArrayList();
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return new MongodbCommandExecutor();
    }
}
