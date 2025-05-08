package ai.magicdb.plugin.mysql.value.sub;

import ai.magicdb.plugin.mysql.value.template.MysqlDmlValueTemplate;
import ai.magicdb.spi.jdbc.DefaultValueProcessor;
import ai.magicdb.spi.model.JDBCDataValue;
import ai.magicdb.spi.model.SQLDataValue;

/**
 * @author: zgq
 * @date: 2024年06月03日 19:43
 */
public class MysqlBinaryProcessor extends DefaultValueProcessor {

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
        byte[] bytes = dataValue.getBytes();
        if (bytes.length == 1) {
            if (bytes[0] >= 32 && bytes[0] <= 126) {
                return new String(bytes);
            }
        }
        return MysqlDmlValueTemplate.wrapHex(dataValue.getBlobHexString());
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return MysqlDmlValueTemplate.wrapHex(dataValue.getBlobHexString());
    }
}
