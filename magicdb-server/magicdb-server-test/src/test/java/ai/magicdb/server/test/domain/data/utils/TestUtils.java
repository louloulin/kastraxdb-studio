package ai.magicdb.server.test.domain.data.utils;

import java.util.concurrent.atomic.AtomicLong;

import ai.magicdb.server.test.domain.data.service.dialect.DialectProperties;
import ai.magicdb.spi.sql.MagicDBContext;
import ai.magicdb.spi.sql.ConnectInfo;

/**
 * Test tool class
 *
 * @author Jiaju Zhuang
 */
public class TestUtils {

    public static final AtomicLong ATOMIC_LONG = new AtomicLong();

    /**
     * a globally unique long
     *
     * @return
     */
    public static long nextLong() {
        return ATOMIC_LONG.incrementAndGet();
    }

    /**
     * If the default value is something like 'DATA'
     * then you need to remove ''
     *
     * @param defaultValue
     * @return
     */
    public static String unWrapperDefaultValue(String defaultValue) {
        if (defaultValue == null) {
            return null;
        }
        if (defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
            if (defaultValue.length() < 2) {
                return defaultValue;
            } else if (defaultValue.length() == 2) {
                return "";
            } else {
                return defaultValue.substring(1, defaultValue.length() - 1);
            }
        }
        return defaultValue;
    }

    public static void buildContext(DialectProperties dialectProperties,Long dataSourceId,Long consoleId){
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setUser(dialectProperties.getUsername());
        connectInfo.setConsoleId(consoleId);
        connectInfo.setDataSourceId(dataSourceId);
        connectInfo.setPassword(dialectProperties.getPassword());
        connectInfo.setDbType(dialectProperties.getDbType());
        connectInfo.setUrl(dialectProperties.getUrl());
        connectInfo.setDatabase(dialectProperties.getDatabaseName());
        connectInfo.setConsoleOwn(false);
        MagicDBContext.putContext(connectInfo);
    }

    public static void remove(){
        MagicDBContext.removeContext();
    }
}
