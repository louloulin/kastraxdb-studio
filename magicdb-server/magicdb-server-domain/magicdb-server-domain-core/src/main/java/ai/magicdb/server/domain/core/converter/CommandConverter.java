package ai.magicdb.server.domain.core.converter;

import ai.magicdb.server.domain.api.param.DlExecuteParam;
import ai.magicdb.spi.model.Command;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class CommandConverter {

    @Mappings({
            @Mapping(target = "script", source = "sql")
    })
    public abstract Command param2model(DlExecuteParam param);
}
