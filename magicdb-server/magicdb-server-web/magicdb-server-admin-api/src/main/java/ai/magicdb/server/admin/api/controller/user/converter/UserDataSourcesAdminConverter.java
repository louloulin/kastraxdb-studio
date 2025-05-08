package ai.magicdb.server.admin.api.controller.user.converter;

import ai.magicdb.server.admin.api.controller.datasource.request.DataSourceAccessBatchCreateRequest;
import ai.magicdb.server.admin.api.controller.user.request.UserPageCommonQueryRequest;
import ai.magicdb.server.admin.api.controller.user.vo.UserDataSourcePageQueryVO;
import ai.magicdb.server.domain.api.enums.AccessObjectTypeEnum;
import ai.magicdb.server.domain.api.enums.DataSourceKindEnum;
import ai.magicdb.server.domain.api.model.DataSourceAccess;
import ai.magicdb.server.domain.api.param.datasource.access.DataSourceAccessBatchCreatParam;
import ai.magicdb.server.domain.api.param.datasource.access.DataSourceAccessComprehensivePageQueryParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * converter
 *
 * @author Jiaju Zhuang
 */
@Mapper(componentModel = "spring", imports = {DataSourceKindEnum.class, AccessObjectTypeEnum.class})
public abstract class UserDataSourcesAdminConverter {

    /**
     * convert
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(source = "userId", target = "accessObjectId"),
        @Mapping(target = "accessObjectType", expression = "java(AccessObjectTypeEnum.USER.name())"),
        @Mapping(source = "searchKey", target = "userOrTeamSearchKey"),
        @Mapping(target = "enableReturnCount", expression = "java(true)"),
    })
    public abstract DataSourceAccessComprehensivePageQueryParam request2param(UserPageCommonQueryRequest request);

    /**
     * convert
     *
     * @param request
     * @return
     */
    public abstract DataSourceAccessBatchCreatParam request2param(DataSourceAccessBatchCreateRequest request);

    /**
     * conversion
     *
     * @param dto
     * @return
     */
    @Mappings({
        @Mapping(target = "userId", source = "accessObjectId"),
    })
    public abstract UserDataSourcePageQueryVO dto2vo(DataSourceAccess dto);

}
