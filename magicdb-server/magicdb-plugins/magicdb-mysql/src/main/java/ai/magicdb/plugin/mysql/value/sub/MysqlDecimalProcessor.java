package ai.magicdb.plugin.mysql.value.sub;

import ai.magicdb.spi.jdbc.DefaultValueProcessor;
import ai.magicdb.spi.model.JDBCDataValue;
import ai.magicdb.spi.model.SQLDataValue;

/**
 * @author: zgq
 * @date: 2024年06月01日 18:01
 */
public class MysqlDecimalProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return dataValue.getValue();
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getBigDecimalString();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return dataValue.getBigDecimalString();
    }
}
