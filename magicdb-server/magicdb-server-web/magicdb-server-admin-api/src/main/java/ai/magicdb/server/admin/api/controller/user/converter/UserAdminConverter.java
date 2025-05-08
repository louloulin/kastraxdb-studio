package ai.magicdb.server.admin.api.controller.user.converter;

import ai.magicdb.server.admin.api.controller.user.request.UserCreateRequest;
import ai.magicdb.server.admin.api.controller.user.request.UserUpdateRequest;
import ai.magicdb.server.admin.api.controller.user.vo.UserPageQueryVO;
import ai.magicdb.server.common.api.controller.request.CommonPageQueryRequest;
import ai.magicdb.server.domain.api.model.User;
import ai.magicdb.server.domain.api.param.user.UserCreateParam;
import ai.magicdb.server.domain.api.param.user.UserPageQueryParam;
import ai.magicdb.server.domain.api.param.user.UserUpdateParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * converter
 *
 * @author Jiaju Zhuang
 */
@Mapper(componentModel = "spring")
public abstract class UserAdminConverter {

    /**
     * conversion
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(target = "enableReturnCount", expression = "java(true)"),
    })
    public abstract UserPageQueryParam request2param(CommonPageQueryRequest request);

    /**
     * conversion
     *
     * @param dto
     * @return
     */
    public abstract UserPageQueryVO dto2vo(User dto);

    /**
     * conversion
     *
     * @param request
     * @return
     */
    public abstract UserCreateParam request2param(UserCreateRequest request);

    /**
     * conversion
     *
     * @param request
     * @return
     */
    public abstract UserUpdateParam request2param(UserUpdateRequest request);
}
