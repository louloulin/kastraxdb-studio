package ai.magicdb.server.web.api.controller.pin.converter;

import ai.magicdb.server.domain.api.param.PinTableParam;
import ai.magicdb.server.web.api.controller.pin.request.PinTableRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class PinWebConverter {


    public abstract PinTableParam req2param(PinTableRequest request);
}
