package ai.magicdb.server.start.controller.oauth;

import ai.magicdb.server.domain.api.enums.RoleCodeEnum;
import ai.magicdb.server.domain.api.enums.ValidStatusEnum;
import jakarta.annotation.Resource;

import ai.magicdb.server.domain.api.model.User;
import ai.magicdb.server.domain.api.service.UserService;
import ai.magicdb.server.start.controller.oauth.request.LoginRequest;
import ai.magicdb.server.tools.base.excption.BusinessException;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.tools.common.model.LoginUser;
import ai.magicdb.server.tools.common.util.ContextUtils;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * Login authorization service
 *
 * @author Jiaju Zhuang
 */
@RestController
@RequestMapping("/api/oauth")
@Slf4j
public class OauthController {

    @Resource
    private UserService userService;

    /**
     * Login with username and password
     *
     * @param request
     * @return
     */
    @PostMapping("login_a")
    public DataResult login(@Validated @RequestBody LoginRequest request) {
        //  Query user
     return DataResult.of(null);
    }

    private boolean validateAdmin(final @NotNull User user) {
        return RoleCodeEnum.ADMIN.getDefaultUserId().equals(user.getId()) && RoleCodeEnum.ADMIN.getPassword().equals(
                user.getPassword());
    }

    private void validateUser(final User user) {
        if (Objects.isNull(user)) {
            throw new BusinessException("oauth.userNameNotExits");
        }
        if (!ValidStatusEnum.VALID.getCode().equals(user.getStatus())) {
            throw new BusinessException("oauth.invalidUserName");
        }
        if (RoleCodeEnum.DESKTOP.getDefaultUserId().equals(user.getId())) {
            throw new BusinessException("oauth.IllegalUserName");
        }
    }

    /**
     * user
     *
     * @return
     */
    @GetMapping("user")
    public DataResult<LoginUser> user() {
        return DataResult.of(getLoginUser());
    }

    /**
     * user
     *
     * @return
     */
    @GetMapping("user_a")
    public DataResult<LoginUser> usera() {
        return DataResult.of(getLoginUser());
    }

    private LoginUser getLoginUser() {
        return ContextUtils.queryLoginUser();
    }

}
