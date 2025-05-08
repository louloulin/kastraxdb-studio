package ai.magicdb.server.test.temp;

import java.util.ArrayList;
import java.util.List;

import ai.magicdb.spi.model.ExecuteResult;
import ai.magicdb.spi.model.Sql;
import jakarta.annotation.Resource;

import ai.magicdb.server.domain.api.param.ConsoleConnectParam;
import ai.magicdb.server.domain.api.param.datasource.DataSourcePreConnectParam;
import ai.magicdb.server.domain.api.param.DlExecuteParam;
import ai.magicdb.server.domain.api.service.ConsoleService;
import ai.magicdb.server.domain.api.service.DataSourceService;
import ai.magicdb.server.domain.api.service.DlTemplateService;
import ai.magicdb.server.domain.api.service.TableService;
import ai.magicdb.server.domain.api.param.SqlAnalyseParam;
import ai.magicdb.server.test.common.BaseTest;
import ai.magicdb.server.test.domain.data.service.dialect.MysqlDialectProperties;
import ai.magicdb.server.tools.base.wrapper.result.ActionResult;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.tools.base.wrapper.result.ListResult;
import com.alibaba.fastjson2.JSON;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class SqlTeset extends BaseTest {

    @Resource
    private TableService tableService;
    @Resource
    private MysqlDialectProperties mysqlDialectProperties;
    @Resource
    private DataSourceService dataSourceService;
    @Resource
    private ConsoleService consoleService;
    @Resource
    private DlTemplateService dlTemplateService;

    @Test
    public void test() {
        // creat
        DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();

        dataSourceCreateParam.setType("MYSQL");
        dataSourceCreateParam.setUrl(mysqlDialectProperties.getUrl());
        dataSourceCreateParam.setUser(mysqlDialectProperties.getUsername());
        dataSourceCreateParam.setPassword(mysqlDialectProperties.getPassword());
        ActionResult actionResult = dataSourceService.preConnect(dataSourceCreateParam);

        DataResult<String> createTable = tableService.createTableExample("MYSQL");
        log.info("sql1：{}", createTable.getData());
        SqlAnalyseParam sqlAnalyseParam = new SqlAnalyseParam();
        sqlAnalyseParam.setDataSourceId(1L);
        sqlAnalyseParam.setSql(createTable.getData());
        List<Sql> sqlList = new ArrayList<>();
        sqlList.add(Sql.builder().sql(createTable.getData()).build());

        // Create a console
        ConsoleConnectParam consoleCreateParam = new ConsoleConnectParam();
        consoleCreateParam.setDataSourceId(1L);
        consoleCreateParam.setConsoleId(1L);
        consoleCreateParam.setDatabaseName(mysqlDialectProperties.getDatabaseName());
        consoleService.createConsole(consoleCreateParam);

        // delete
        DlExecuteParam templateQueryParam = new DlExecuteParam();
        templateQueryParam.setConsoleId(1L);
        templateQueryParam.setDataSourceId(1L);
        templateQueryParam.setSql("drop table test;");
        ListResult<ExecuteResult> executeResult = dlTemplateService.execute(templateQueryParam);
        log.info("result:{}", JSON.toJSONString(executeResult));

        // Create table structure
        templateQueryParam = new DlExecuteParam();
        templateQueryParam.setConsoleId(1L);
        templateQueryParam.setDataSourceId(1L);
        templateQueryParam.setSql(sqlList.get(0).getSql());
        executeResult = dlTemplateService.execute(templateQueryParam);
        log.info("result:{}", JSON.toJSONString(executeResult));
    }
}
