package ai.magicdb.server.start.config.config;

import java.io.IOException;
import java.util.Enumeration;

import ai.magicdb.server.domain.repository.Dbutils;
import com.alibaba.fastjson2.JSON;

import ai.magicdb.server.domain.api.enums.RoleCodeEnum;
import ai.magicdb.server.domain.api.enums.ValidStatusEnum;
import ai.magicdb.server.domain.api.model.User;
import ai.magicdb.server.domain.api.service.TeamUserService;
import ai.magicdb.server.domain.api.service.UserService;
import ai.magicdb.server.domain.core.cache.CacheKey;
import ai.magicdb.server.domain.core.cache.MemoryCacheManage;
import ai.magicdb.server.tools.base.constant.SymbolConstant;
import ai.magicdb.server.tools.base.excption.BusinessException;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.server.tools.common.config.MagicdbProperties;
import ai.magicdb.server.tools.common.enums.ModeEnum;
import ai.magicdb.server.tools.common.exception.PermissionDeniedBusinessException;
import ai.magicdb.server.tools.common.exception.RedirectBusinessException;
import ai.magicdb.server.tools.common.model.Context;
import ai.magicdb.server.tools.common.model.LoginUser;
import ai.magicdb.server.tools.common.util.ContextUtils;
import ai.magicdb.server.tools.common.util.I18nUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web project configuration
 *
 * @author Shi Yi
 */
@Configuration
@Slf4j
public class MagicdbWebMvcConfigurer implements WebMvcConfigurer {

    /**
     * api prefix
     */
    private static final String API_PREFIX = "/api/";

    /**
     * Globally released url
     */
    private static final String[] FRONT_PERMIT_ALL = new String[] {"/favicon.ico", "/error", "/static/**",
        "/api/system", "/login"};

    @Resource
    private UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // All requests try to add user information
        registry.addInterceptor(new AsyncHandlerInterceptor() {
                @Override
                public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                    @NotNull Object handler) {
                    Dbutils.setSession();
                    Long userId = RoleCodeEnum.DESKTOP.getDefaultUserId();
                    Long finalUserId = userId;
                    LoginUser loginUser = MemoryCacheManage.computeIfAbsent(CacheKey.getLoginUserKey(userId), () -> {
                        User user = userService.query(finalUserId).getData();
                        if (user == null) {
                            return null;
                        }
                        boolean iaAdmin = RoleCodeEnum.ADMIN.getCode().equals(user.getRoleCode());

                        return LoginUser.builder()
                            .id(user.getId())
                            .nickName(user.getNickName())
                            .admin(iaAdmin)
                            .roleCode(user.getRoleCode())
                            .build();
                    });

                    if (loginUser == null) {
                        // Indicates that the user may have been deleted
                        return true;
                    }
                    loginUser.setToken(userId.toString());

                    ContextUtils.setContext(Context.builder()
                        .loginUser(loginUser)
                        .build());
                    return true;
                }

                @Override
                public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                    Exception ex) throws Exception {
                    // Remove login information
                    ContextUtils.removeContext();
                    Dbutils.removeSession();
                }
            })
            .order(1)
            .addPathPatterns("/**")
            .excludePathPatterns(FRONT_PERMIT_ALL);

        // Verify login information

    }

}
