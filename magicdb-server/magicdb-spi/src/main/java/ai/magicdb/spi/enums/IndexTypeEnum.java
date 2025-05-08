package ai.magicdb.spi.enums;


import ai.magicdb.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * Index type
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum IndexTypeEnum implements BaseEnum<String> {
    /**
     * primary key
     */
    PRIMARY_KEY("primary key"),

    /**
     * Ordinary index
     */
    NORMAL("Ordinary index"),

    /**
     * unique index
     */
    UNIQUE("unique index"),
    ;

    final String description;

    IndexTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
