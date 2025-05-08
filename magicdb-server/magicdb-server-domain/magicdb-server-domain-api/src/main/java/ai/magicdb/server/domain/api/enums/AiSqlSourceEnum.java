package ai.magicdb.server.domain.api.enums;


import ai.magicdb.server.tools.base.enums.BaseEnum;

import lombok.Getter;

/**
 * AI model type selected by AI SQL
 *
 * @author moji
 */
@Getter
public enum AiSqlSourceEnum implements BaseEnum<String> {
    /**
     * OPENAI
     */
    OPENAI( "OPENAI"),

    /**
     * RESTAI
     */
    RESTAI("RESTAI"),

    /**
     * AZURE OPENAI
     */
    AZUREAI("AZURE OPENAI"),

    /**
     * MAGICDB OPENAI
     */
    MAGICDBAI("MAGICDB OPENAI"),

    /**
     * CLAUDE AI
     */
    CLAUDEAI("CLAUDE AI"),

    /**
     * WNEXIN AI
     */
    WENXINAI("WENXIN AI"),

    /**
     * BAICHUAN AI
     */
    BAICHUANAI("BAICHUAN AI"),

    /**
     * ZHIPU AI
     */
    ZHIPUAI("ZHIPU AI"),

    /**
     * TONGYIQIANWEN AI
     */
    TONGYIQIANWENAI("TONGYIQIANWEN AI"),

    /**
     * FAST CHAT AI
     */
    FASTCHATAI("FAST CHAT AI"),

    ;

    final String description;


    AiSqlSourceEnum(String description) {
        this.description = description;
    }

    /**
     * Get enum by name
     *
     * @param name
     * @return
     */
    public static AiSqlSourceEnum getByName(String name) {
        for (AiSqlSourceEnum dbTypeEnum : AiSqlSourceEnum.values()) {
            if (dbTypeEnum.name().equals(name)) {
                return dbTypeEnum;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return this.name();
    }

}
