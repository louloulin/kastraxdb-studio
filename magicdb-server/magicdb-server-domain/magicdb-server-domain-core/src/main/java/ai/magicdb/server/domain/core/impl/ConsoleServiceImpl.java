package ai.magicdb.server.domain.core.impl;

import ai.magicdb.server.domain.api.param.ConsoleConnectParam;
import ai.magicdb.server.domain.api.service.ConsoleService;
import ai.magicdb.server.domain.api.param.ConsoleCloseParam;
import ai.magicdb.spi.sql.MagicDBContext;
import ai.magicdb.spi.sql.SQLExecutor;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;

import org.springframework.stereotype.Service;

/**
 * @author moji
 * @version DataSourceCoreServiceImpl.java, v 0.1 September 23, 2022 15:51 moji Exp $
 * @date 2022/09/23
 */
@Service
public class ConsoleServiceImpl implements ConsoleService {
    @Override
    public ActionResult createConsole(ConsoleConnectParam param) {
        MagicDBContext.getDBManage().connectDatabase(MagicDBContext.getConnection(),param.getDatabaseName());
        return ActionResult.isSuccess();
    }

    @Override
    public ActionResult closeConsole(ConsoleCloseParam param) {
        return ActionResult.isSuccess();
    }

}
