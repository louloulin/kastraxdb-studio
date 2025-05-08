package ai.magicdb.spi.enums;

import ai.magicdb.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * sql type
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum SqlTypeEnum implements BaseEnum<String> {
    /**
     * Check for phrases
     */
    SELECT("Check for phrases"),

    /**
     * unknow
     */
    UNKNOWN("unknow"),

    ;

    final String description;

    SqlTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
