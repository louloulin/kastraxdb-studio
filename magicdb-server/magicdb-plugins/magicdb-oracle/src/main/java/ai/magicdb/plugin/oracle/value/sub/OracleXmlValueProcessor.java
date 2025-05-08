package ai.magicdb.plugin.oracle.value.sub;

import ai.magicdb.plugin.oracle.value.template.OracleDmlValueTemplate;
import ai.magicdb.spi.jdbc.DefaultValueProcessor;
import ai.magicdb.spi.model.JDBCDataValue;
import ai.magicdb.spi.model.SQLDataValue;

/**
 * @author: zgq
 * @date: 2024年06月21日 12:55
 */
public class OracleXmlValueProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return OracleDmlValueTemplate.wrapXml(dataValue.getValue());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getStringValue();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return OracleDmlValueTemplate.wrapXml(dataValue.getString());
    }
}
