package ai.magicdb.server.domain.core.impl;

import ai.magicdb.server.domain.api.service.ViewService;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.tools.base.wrapper.result.ListResult;
import ai.magicdb.spi.MetaData;
import ai.magicdb.spi.model.Table;
import ai.magicdb.spi.sql.MagicDBContext;
import org.springframework.stereotype.Service;

@Service
public class ViewServiceImpl implements ViewService {

    @Override
    public ListResult<Table> views(String databaseName, String schemaName) {
        return ListResult.of(MagicDBContext.getMetaData().views(MagicDBContext.getConnection(),databaseName, schemaName));
    }

    @Override
    public DataResult<Table> detail(String databaseName, String schemaName, String tableName) {
        MetaData metaSchema = MagicDBContext.getMetaData();
        Table table = metaSchema.view(MagicDBContext.getConnection(), databaseName, schemaName, tableName);
        return DataResult.of(table);
    }

}
