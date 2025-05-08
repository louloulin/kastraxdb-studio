package ai.magicdb.server.domain.core.impl;

import ai.magicdb.server.domain.api.service.FunctionService;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.tools.base.wrapper.result.ListResult;
import ai.magicdb.spi.model.Function;
import ai.magicdb.spi.sql.MagicDBContext;
import org.springframework.stereotype.Service;

@Service
public class FunctionServiceImpl implements FunctionService {
    @Override
    public ListResult<Function> functions(String databaseName, String schemaName) {
        return ListResult.of(MagicDBContext.getMetaData().functions(MagicDBContext.getConnection(),databaseName, schemaName));
    }

    @Override
    public DataResult<Function> detail(String databaseName, String schemaName, String functionName) {
        return DataResult.of(MagicDBContext.getMetaData().function(MagicDBContext.getConnection(), databaseName, schemaName, functionName));
    }

    @Override
    public ActionResult delete(String databaseName, String schemaName, Function function) {
        MagicDBContext.getDBManage().deleteFunction(MagicDBContext.getConnection(), databaseName, schemaName, function);
        return ActionResult.isSuccess();
    }
}
