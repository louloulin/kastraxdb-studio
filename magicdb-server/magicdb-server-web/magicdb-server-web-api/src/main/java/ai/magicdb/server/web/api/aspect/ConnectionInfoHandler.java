package ai.magicdb.server.web.api.aspect;

import ai.magicdb.server.domain.api.model.DataSource;
import ai.magicdb.server.domain.api.service.DataSourceAccessBusinessService;
import ai.magicdb.server.domain.api.service.DataSourceService;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.tools.common.exception.ParamBusinessException;
import ai.magicdb.server.tools.common.util.ContextUtils;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceBaseRequestInfo;
import ai.magicdb.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;
import ai.magicdb.spi.config.DriverConfig;
import ai.magicdb.spi.sql.MagicDBContext;
import ai.magicdb.spi.sql.ConnectInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jipengfei
 * @version : ConnectionInfoHandler.java
 */
@Component
@Aspect
@Slf4j
public class ConnectionInfoHandler {

    @Autowired
    private DataSourceService dataSourceService;
    @Resource
    private DataSourceAccessBusinessService dataSourceAccessBusinessService;

    @Around("within(@ai.magicdb.server.web.api.aspect.ConnectionInfoAspect *)")
    public Object connectionInfoHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            Object[] params = proceedingJoinPoint.getArgs();
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof DataSourceBaseRequest) {
                        Long dataSourceId = ((DataSourceBaseRequest) param).getDataSourceId();
                        String schemaName = ((DataSourceBaseRequest) param).getSchemaName();
                        String database = ((DataSourceBaseRequest) param).getDatabaseName();
                        MagicDBContext.putContext(toInfo(dataSourceId, database, null, schemaName));
                    } else if (param instanceof DataSourceConsoleRequestInfo) {
                        Long dataSourceId = ((DataSourceConsoleRequestInfo) param).getDataSourceId();
                        Long consoleId = ((DataSourceConsoleRequestInfo) param).getConsoleId();
                        String database = ((DataSourceConsoleRequestInfo) param).getDatabaseName();
                        MagicDBContext.putContext(toInfo(dataSourceId, database, consoleId, null));
                    } else if (param instanceof DataSourceBaseRequestInfo) {
                        Long dataSourceId = ((DataSourceBaseRequestInfo) param).getDataSourceId();
                        String database = ((DataSourceBaseRequestInfo) param).getDatabaseName();
                        MagicDBContext.putContext(toInfo(dataSourceId, database));
                    }
                }
            }
            return proceedingJoinPoint.proceed();
        } finally {
            MagicDBContext.removeContext();
        }
    }

    public ConnectInfo toInfo(Long dataSourceId, String database, Long consoleId, String schemaName) {
        DataResult<DataSource> result = dataSourceService.queryById(dataSourceId);
        DataSource dataSource = result.getData();
        if (!result.success() || dataSource == null) {
            throw new ParamBusinessException("dataSourceId");
        }

        // Verify permissions
        dataSourceAccessBusinessService.checkPermission(dataSource);

        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setAlias(dataSource.getAlias());
        connectInfo.setUser(dataSource.getUserName());
        connectInfo.setConsoleId(consoleId);
        connectInfo.setDataSourceId(dataSourceId);
        connectInfo.setPassword(dataSource.getPassword());
        connectInfo.setDbType(dataSource.getType());
        connectInfo.setUrl(dataSource.getUrl());
        connectInfo.setDatabase(database);
        connectInfo.setSchemaName(schemaName);
        connectInfo.setConsoleOwn(false);
        connectInfo.setDriver(dataSource.getDriver());
        connectInfo.setSsh(dataSource.getSsh());
        connectInfo.setSsl(dataSource.getSsl());
        connectInfo.setJdbc(dataSource.getJdbc());
        connectInfo.setExtendInfo(dataSource.getExtendInfo());
        connectInfo.setUrl(dataSource.getUrl());
        connectInfo.setPort(StringUtils.isNotBlank(dataSource.getPort()) ? Integer.parseInt(dataSource.getPort()) : null);
        connectInfo.setHost(dataSource.getHost());
        connectInfo.setLoginUser(ContextUtils.getLoginUser().getId() + "");
        DriverConfig driverConfig = dataSource.getDriverConfig();
        if (driverConfig != null && driverConfig.notEmpty()) {
            connectInfo.setDriverConfig(driverConfig);
        }
        return connectInfo;
    }

    public ConnectInfo toInfo(Long dataSourceId, String database) {
        return toInfo(dataSourceId, database, null, null);
    }

}