package ai.magicdb.plugin.mongodb;

import ai.magicdb.spi.model.Command;
import ai.magicdb.spi.model.ExecuteResult;
import ai.magicdb.spi.sql.SQLExecutor;

import java.util.List;

public class MongodbCommandExecutor extends SQLExecutor {

    @Override
    public List<ExecuteResult> executeSelectTable(Command command) {
        String sql = "db." + command.getTableName() + ".find()";
        command.setScript(sql);
        return execute(command);
    }
}
