package ai.magicdb.server.tools.common.enums;

import ai.magicdb.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * model
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum ModeEnum implements BaseEnum<String> {
    /**
     * DESKTOP
     */
    DESKTOP("DESKTOP"),

    /**
     * WEB
     */
    WEB("WEB"),

    ;
    final String description;

    ModeEnum(String description) {
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
