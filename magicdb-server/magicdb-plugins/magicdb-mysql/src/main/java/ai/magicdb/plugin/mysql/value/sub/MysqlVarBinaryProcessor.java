package ai.magicdb.plugin.mysql.value.sub;

import ai.magicdb.plugin.mysql.value.template.MysqlDmlValueTemplate;
import ai.magicdb.spi.jdbc.DefaultValueProcessor;
import ai.magicdb.spi.model.JDBCDataValue;
import ai.magicdb.spi.model.SQLDataValue;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: zgq
 * @date: 2024年06月03日 20:48
 */
@Slf4j
public class MysqlVarBinaryProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        String value = dataValue.getValue();
        if (value.startsWith("0x")) {
            return value;
        }
        return MysqlDmlValueTemplate.wrapHex(dataValue.getBlobHexString());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getBlobString();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return MysqlDmlValueTemplate.wrapHex(dataValue.getBlobHexString());
    }

}

