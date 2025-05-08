package ai.magicdb.server.domain.core.converter;

import ai.magicdb.server.domain.api.param.TableVectorParam;
import ai.magicdb.server.domain.repository.entity.TableVectorMappingDO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class TableConverter {

    /**
     * TableVectorParam to TableVectorMappingDO
     *
     * @param param
     * @return
     */
    public abstract TableVectorMappingDO toTableVectorMappingDO(TableVectorParam param);
}
