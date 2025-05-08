package ai.magicdb.server.admin.api.controller.team.converter;

import ai.magicdb.server.admin.api.controller.team.request.TeamCreateRequest;
import ai.magicdb.server.admin.api.controller.team.request.TeamUpdateRequest;
import ai.magicdb.server.admin.api.controller.team.vo.TeamPageQueryVO;
import ai.magicdb.server.common.api.controller.request.CommonPageQueryRequest;
import ai.magicdb.server.domain.api.enums.DataSourceKindEnum;
import ai.magicdb.server.domain.api.model.Team;
import ai.magicdb.server.domain.api.param.team.TeamCreateParam;
import ai.magicdb.server.domain.api.param.team.TeamPageQueryParam;
import ai.magicdb.server.domain.api.param.team.TeamUpdateParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * converter
 *
 * @author Jiaju Zhuang
 */
@Mapper(componentModel = "spring",imports = {DataSourceKindEnum.class})
public abstract class TeamAdminConverter {


    /**
     * conversion
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(target = "enableReturnCount", expression = "java(true)"),
    })
    public abstract TeamPageQueryParam request2param(CommonPageQueryRequest request);


    /**
     * conversion
     *
     * @param dto
     * @return
     */
    public abstract TeamPageQueryVO dto2vo(Team dto);


    /**
     * conversion
     *
     * @param request
     * @return
     */
    public abstract TeamCreateParam request2param(TeamCreateRequest request);


    /**
     * conversion
     *
     * @param request
     * @return
     */
    public abstract TeamUpdateParam request2param(TeamUpdateRequest request);
}
