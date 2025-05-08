package ai.magicdb.plugin.oracle.value.sub;

import ai.magicdb.plugin.oracle.value.template.OracleDmlValueTemplate;
import ai.magicdb.spi.jdbc.DefaultValueProcessor;
import ai.magicdb.spi.model.JDBCDataValue;
import ai.magicdb.spi.model.SQLDataValue;

/**
 * @author: zgq
 * @date: 2024年06月05日 18:56
 */
public class OracleIntervalDSProcessor extends DefaultValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return OracleDmlValueTemplate.wrapIntervalDayToSecond(dataValue.getValue(), dataValue.getPrecision(), dataValue.getScale());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getStringValue();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return OracleDmlValueTemplate.wrapIntervalDayToSecond(convertJDBCValueByType(dataValue), dataValue.getPrecision(), dataValue.getScale());
    }

}
