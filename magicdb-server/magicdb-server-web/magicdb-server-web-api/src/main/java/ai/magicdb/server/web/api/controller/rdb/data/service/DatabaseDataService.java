package ai.magicdb.server.web.api.controller.rdb.data.service;

import ai.magicdb.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;

/**
 * @author: zgq
 * @date: 2024年06月08日 10:32
 */
public interface DatabaseDataService {

    DataResult<Long> doExportAsync(DatabaseExportDataParam databaseExportDataParam);
}
