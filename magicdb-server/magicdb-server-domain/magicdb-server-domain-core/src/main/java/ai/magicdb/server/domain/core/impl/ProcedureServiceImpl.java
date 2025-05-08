package ai.magicdb.server.domain.core.impl;

import ai.magicdb.server.domain.api.service.ProcedureService;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.tools.base.wrapper.result.ListResult;
import ai.magicdb.spi.model.Procedure;
import ai.magicdb.spi.sql.MagicDBContext;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ProcedureServiceImpl implements ProcedureService {

    @Override
    public ListResult<Procedure> procedures(String databaseName, String schemaName) {
        return ListResult.of(MagicDBContext.getMetaData().procedures(MagicDBContext.getConnection(),databaseName, schemaName));
    }

    @Override
    public DataResult<Procedure> detail(String databaseName, String schemaName, String procedureName) {
        return DataResult.of(MagicDBContext.getMetaData().procedure(MagicDBContext.getConnection(), databaseName, schemaName, procedureName));
    }
    @Override
    public ActionResult update(String databaseName, String schemaName, Procedure procedure) throws SQLException {
        MagicDBContext.getDBManage().updateProcedure(MagicDBContext.getConnection(), databaseName, schemaName, procedure);
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult delete(String databaseName, String schemaName, Procedure procedure) {
        MagicDBContext.getDBManage().deleteProcedure(MagicDBContext.getConnection(), databaseName, schemaName, procedure);
        return ActionResult.isSuccess();
    }
}
