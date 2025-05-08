
package ai.magicdb.server.web.api.controller.user.converter;

import java.util.List;

import ai.magicdb.server.domain.api.model.User;
import ai.magicdb.server.web.api.controller.user.request.UserCreateRequest;
import ai.magicdb.server.web.api.controller.user.request.UserUpdateRequest;
import ai.magicdb.server.web.api.controller.user.vo.UserVO;

import org.mapstruct.Mapper;

/**
 * @author jipengfei
 * @version : UserWebConverter.java
 */
@Mapper(componentModel = "spring")
public abstract class UserWebConverter {
    /**
     * Convert
     *
     * @param user
     * @return
     */
    public abstract UserVO dto2vo(User user);

    /**
     *
     * @param user
     * @return
     */
    public abstract List<UserVO> dto2vo(List<User> user);

    /**
     *
     * @param createRequest
     * @return
     */
    public abstract User createRequest2dto(UserCreateRequest createRequest);

    /**
     *
     * @param updateRequest
     * @return
     */
    public abstract User updateRequest2dto(UserUpdateRequest updateRequest);

}