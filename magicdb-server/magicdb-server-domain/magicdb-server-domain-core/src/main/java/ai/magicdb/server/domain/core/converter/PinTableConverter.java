package ai.magicdb.server.domain.core.converter;

import ai.magicdb.server.domain.api.param.PinTableParam;
import ai.magicdb.server.domain.api.param.TablePageQueryParam;
import ai.magicdb.server.domain.repository.entity.PinTableDO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class PinTableConverter {

    /**
     *
     * @param param
     * @return
     */
    public abstract PinTableDO param2do(PinTableParam param);



    public abstract PinTableParam toPinTableParam (TablePageQueryParam param);
}
