package ai.magicdb.plugin.mariadb.value.sub;

import ai.magicdb.server.tools.common.util.EasyStringUtils;
import ai.magicdb.spi.jdbc.DefaultValueProcessor;
import ai.magicdb.spi.model.JDBCDataValue;
import ai.magicdb.spi.model.SQLDataValue;

/**
 * @author: zgq
 * @date: 2024年06月01日 18:26
 */
public class MariaDBTimestampProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return EasyStringUtils.quoteString(dataValue.getValue());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getStringValue();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return EasyStringUtils.quoteString(dataValue.getStringValue());
    }
}
