package ai.magicdb.server.test.domain.data.service;

import java.util.List;

import ai.magicdb.spi.model.Database;
import jakarta.annotation.Resource;

import ai.magicdb.server.domain.api.param.datasource.DataSourcePreConnectParam;
import ai.magicdb.server.domain.api.service.DataSourceService;
import ai.magicdb.server.domain.api.service.DatabaseService;
import ai.magicdb.server.domain.api.param.datasource.DatabaseQueryAllParam;
import ai.magicdb.server.test.common.BaseTest;
import ai.magicdb.server.test.domain.data.service.dialect.DialectProperties;
import ai.magicdb.server.test.domain.data.utils.TestUtils;
import ai.magicdb.server.tools.base.wrapper.result.ListResult;
import com.alibaba.fastjson2.JSON;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Database testing
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class DatabaseOperationsTest extends BaseTest {
    @Resource
    private DataSourceService dataSourceService;
    @Autowired
    private List<DialectProperties> dialectPropertiesList;
    @Resource
    private DatabaseService databaseService;

    @Test
    @Order(1)
    public void queryAll() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            String dbTypeEnum = dialectProperties.getDbType();
            Long dataSourceId = TestUtils.nextLong();

            // Prepare context
            putConnect(dialectProperties.getUrl(), dialectProperties.getUsername(), dialectProperties.getPassword(),
                dialectProperties.getDbType(), dialectProperties.getDatabaseName(), dataSourceId, null);

            DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();

            dataSourceCreateParam.setType(dbTypeEnum);
            dataSourceCreateParam.setUrl(dialectProperties.getUrl());
            dataSourceCreateParam.setUser(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            dataSourceService.preConnect(dataSourceCreateParam);

            DatabaseQueryAllParam databaseQueryAllParam = new DatabaseQueryAllParam();
            databaseQueryAllParam.setDataSourceId(dataSourceId);
            ListResult<Database> databaseList = databaseService.queryAll(databaseQueryAllParam);
            log.info("Querying the database returns: {}", JSON.toJSONString(databaseList));

            Database Database = databaseList.getData().stream()
                .filter(database -> dialectProperties.getDatabaseName().equals(database.getName()))
                .findFirst()
                .orElse(null);
            Assertions.assertNotNull(Database, "Query database failed");

            removeConnect();
        }
    }

}
