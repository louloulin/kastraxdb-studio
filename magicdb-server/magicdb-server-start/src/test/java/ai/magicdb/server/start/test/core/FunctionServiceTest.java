package ai.magicdb.server.start.test.core;

import ai.magicdb.server.domain.api.service.FunctionService;
import ai.magicdb.server.domain.repository.Dbutils;
import ai.magicdb.server.start.test.TestApplication;
import ai.magicdb.server.start.test.dialect.DialectProperties;
import ai.magicdb.server.start.test.dialect.TestUtils;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.tools.base.wrapper.result.ListResult;
import ai.magicdb.server.tools.common.model.Context;
import ai.magicdb.server.tools.common.model.LoginUser;
import ai.magicdb.server.tools.common.util.ContextUtils;
import ai.magicdb.spi.model.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Juechen
 * @version : FunctionServiceTest.java
 */
public class FunctionServiceTest extends TestApplication {

    @Autowired
    private FunctionService functionService;

    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Test
    public void testFunctions() {

        userLoginIdentity(false, 3L);

        for (DialectProperties dialectProperties : dialectPropertiesList) {
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            ListResult<Function> functions = functionService.functions(dialectProperties.getDatabaseName(), null);
            Assertions.assertTrue(functions.getSuccess(), functions.errorMessage());

            if (dialectProperties.getDbType().equals("MYSQL")) {
                DataResult<Function> detail = functionService.detail(dialectProperties.getDatabaseName(), null, "add_numbers");
                Assertions.assertTrue(detail.getSuccess(), detail.errorMessage());
            }

        }
    }


    /**
     * Save the current user identity (administrator or normal user) and user ID to the context and database session for subsequent use.
     *
     * @param isAdmin
     * @param userId
     */
    private static void userLoginIdentity(boolean isAdmin, Long userId) {
        Context context = Context.builder().loginUser(
                LoginUser.builder().admin(isAdmin).id(userId).build()
        ).build();
        ContextUtils.setContext(context);
        Dbutils.setSession();
    }
}
