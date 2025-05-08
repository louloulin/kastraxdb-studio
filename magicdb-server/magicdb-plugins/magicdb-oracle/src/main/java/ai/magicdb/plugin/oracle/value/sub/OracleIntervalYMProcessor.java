package ai.magicdb.plugin.oracle.value.sub;

import ai.magicdb.plugin.oracle.value.template.OracleDmlValueTemplate;
import ai.magicdb.spi.jdbc.DefaultValueProcessor;
import ai.magicdb.spi.model.JDBCDataValue;
import ai.magicdb.spi.model.SQLDataValue;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年06月05日 18:58
 */
public class OracleIntervalYMProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return OracleDmlValueTemplate.wrapIntervalYearToMonth(dataValue.getValue(), dataValue.getPrecision());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getStringValue();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return OracleDmlValueTemplate.wrapIntervalYearToMonth(dataValue.getStringValue(), dataValue.getPrecision());
    }
}
