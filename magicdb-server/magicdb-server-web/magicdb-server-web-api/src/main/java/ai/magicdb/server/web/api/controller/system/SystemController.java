/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package ai.magicdb.server.web.api.controller.system;

import ai.magicdb.server.domain.api.model.Config;
import ai.magicdb.server.domain.api.param.SystemConfigParam;
import ai.magicdb.server.domain.api.service.ConfigService;
import ai.magicdb.server.domain.core.cache.CacheManage;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.tools.common.config.MagicdbProperties;
import ai.magicdb.server.tools.common.enums.ModeEnum;
import ai.magicdb.server.tools.common.model.ConfigJson;
import ai.magicdb.server.tools.common.util.ConfigUtils;
import ai.magicdb.server.tools.common.util.EasyEnumUtils;
import ai.magicdb.server.web.api.controller.ai.magicdb.client.MagicdbAIClient;
import ai.magicdb.server.web.api.controller.system.util.SystemUtils;
import ai.magicdb.server.web.api.controller.system.vo.AppVersionVO;
import ai.magicdb.server.web.api.controller.system.vo.SystemVO;
import ai.magicdb.spi.ssh.SSHManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;


/**
 * @author jipengfei
 * @version : HomeController.java, v 0.1 September 18, 2022 14:52 jipengfei Exp $
 */
@RestController
@RequestMapping("/api/system")
@Slf4j
public class SystemController {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MagicdbProperties magicdbProperties;

    @Autowired
    private ConfigService configService;

    /**
     * Check if the test is successful
     *
     * @return
     */
    @GetMapping
    public DataResult<SystemVO> get() {
        String clientVersion = System.getProperty("client.version");
        String version = ConfigUtils.getLatestLocalVersion();
        log.error("clientVersion:{},version:{}", clientVersion, version);
        if (!StringUtils.equals(clientVersion, version) && !StringUtils.isEmpty(clientVersion)) {
            stop();
            return null;
        } else {
            ConfigJson configJson = ConfigUtils.getConfig();
            return DataResult.of(SystemVO.builder()
                    .systemUuid(configJson.getSystemUuid())
                    .build());
        }
    }

    private static final String UPDATE_TYPE = "client_update_type";

    @GetMapping("/get_latest_version")
    public DataResult<AppVersionVO> getLatestVersion(String currentVersion) {
        ModeEnum mode = EasyEnumUtils.getEnum(ModeEnum.class, System.getProperty("magicdb.mode"));
        if (mode != ModeEnum.DESKTOP) {
            // In this mode, no user login is required, so only local access is available
            return DataResult.of(null);
        }
        String user = "";
        DataResult<Config> dataResult = configService.find(MagicdbAIClient.MAGICDB_OPENAI_KEY);
        if (dataResult.getData() != null) {
            user = dataResult.getData().getContent();
        }
        AppVersionVO appVersionVO = SystemUtils.getLatestVersion(currentVersion, "manual", user);
        if (appVersionVO == null) {
            appVersionVO = new AppVersionVO();
            appVersionVO.setVersion(currentVersion);
            appVersionVO.setType("manual");
        }
        DataResult<Config> updateType = configService.find(UPDATE_TYPE);
        if (updateType.getData() != null) {
            appVersionVO.setType(updateType.getData().getContent());
        }
        // In this mode, no user login is required, so only local access is available
        appVersionVO.setDesktop(true);
        return DataResult.of(appVersionVO);
    }

    @PostMapping("/update_desktop_version")
    public DataResult<String> updateDesktopVersion(@RequestBody AppVersionVO version) {
        new Thread(() -> {
            SystemUtils.upgrade(version);
        }).start();
        return DataResult.of(version.getVersion());
    }

    @GetMapping("/is_update_success")
    public DataResult<Boolean> isUpdateSuccess(String version) {
        String localVersion = ConfigUtils.getLocalVersion();
        if (StringUtils.isEmpty(localVersion)) {
            return DataResult.of(false);
        }
        return DataResult.of(localVersion.equals(version));
    }

    @PostMapping("/set_update_type")
    public ActionResult setUpdateType(@RequestBody String updateType) {
        SystemConfigParam systemConfigParam = new SystemConfigParam();
        systemConfigParam.setCode(UPDATE_TYPE);
        systemConfigParam.setContent(updateType);
        systemConfigParam.setSummary("client update type");
        configService.createOrUpdate(systemConfigParam);
        return ActionResult.isSuccess();
    }

    /**
     * Get the current version number
     *
     * @return
     */
    @GetMapping("/get-version-a")
    public DataResult<String> getVersion() {
        return DataResult.of(magicdbProperties.getVersion());
    }

    /**
     * Exit service
     */
    @RequestMapping("/stop")
    public DataResult<String> stop(boolean forceQuit) {
        log.info("Exit application");
        if (forceQuit) {
            stop();
        } else {
//            String clientVersion = System.getProperty("client.version");
//            String version = ConfigUtils.getLatestLocalVersion();
//            log.error("clientVersion:{},version:{}", clientVersion, version);
//            if (!StringUtils.equals(clientVersion, version)) {
            stop();
            //}
        }
        return DataResult.of("ok");
    }

    private void stop() {
        new Thread(() -> {
            //  Will exit the background after 100ms
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Start exiting Spring application");
            SSHManager.close();
            try {
                SpringApplication.exit(applicationContext);
            } catch (Exception ignore) {
            }
            // It is possible that SpringApplication.exit will fail to exit
            // Direct system exit
            log.info("Start exiting system applications");
            CacheManage.close();
            try {
                System.exit(0);
            } catch (Exception ignore) {
            }

        }).start();
    }
}
