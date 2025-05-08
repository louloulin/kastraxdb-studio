package ai.magicdb.server.domain.core.impl;

import ai.magicdb.server.domain.api.service.TriggerService;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.tools.base.wrapper.result.ListResult;
import ai.magicdb.spi.model.Trigger;
import ai.magicdb.spi.sql.MagicDBContext;
import org.springframework.stereotype.Service;

@Service
public class TriggerServiceImpl implements TriggerService {
    @Override
    public ListResult<Trigger> triggers(String databaseName, String schemaName) {
        return ListResult.of(MagicDBContext.getMetaData().triggers(MagicDBContext.getConnection(),databaseName, schemaName));
    }

    @Override
    public DataResult<Trigger> detail(String databaseName, String schemaName, String triggerName) {
        return DataResult.of(MagicDBContext.getMetaData().trigger(MagicDBContext.getConnection(), databaseName, schemaName, triggerName));
    }
}
