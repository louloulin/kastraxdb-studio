package ai.magicdb.server.web.api.controller.rdb.converter;

import ai.magicdb.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.magicdb.server.domain.api.param.datasource.DatabaseExportParam;
import ai.magicdb.server.web.api.controller.rdb.request.DatabaseCreateRequest;
import ai.magicdb.server.web.api.controller.rdb.request.DatabaseExportDataRequest;
import ai.magicdb.server.web.api.controller.rdb.request.DatabaseExportRequest;
import ai.magicdb.spi.model.Database;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class DatabaseConverter {

    public abstract Database request2param(DatabaseCreateRequest request);

    public abstract DatabaseExportParam request2param(DatabaseExportRequest request);

    public abstract DatabaseExportDataParam request2param(DatabaseExportDataRequest request);
}
