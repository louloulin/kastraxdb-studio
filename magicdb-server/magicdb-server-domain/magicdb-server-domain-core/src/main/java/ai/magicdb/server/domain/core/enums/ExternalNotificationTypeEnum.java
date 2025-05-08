package ai.magicdb.server.domain.core.enums;

import ai.magicdb.server.domain.api.service.WebhookSender;
import ai.magicdb.server.domain.core.notification.DingTalkWebhookSender;
import ai.magicdb.server.domain.core.notification.LarkWebhookSender;
import ai.magicdb.server.domain.core.notification.WeComWebhookSender;
import ai.magicdb.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * @author Juechen
 * @version : ExternalNotificationTypeEnum.java
 */
@Getter
public enum ExternalNotificationTypeEnum implements BaseEnum<String> {

    /**
     * 企业微信
     */
    WECOM("WeCom", WeComWebhookSender.class),

    /**
     * 钉钉
     */
    DINGTALK("DingTalk", DingTalkWebhookSender.class),

    /**
     * 飞书
     */
    LARK("Lark", LarkWebhookSender.class),

    ;

    final String description;

    final Class<? extends WebhookSender> webhookSender;


    @Override
    public String getCode() {
        return this.name();
    }

    public static WebhookSender getWebhookSender(String platformType) {
        String lowerCasePlatformType = platformType.toLowerCase();
        switch (lowerCasePlatformType) {
            case "wecom":
                return new WeComWebhookSender();
            case "dingtalk":
                return new DingTalkWebhookSender();
            case "lark":
                return new LarkWebhookSender();
            default:
                return null;
        }
    }

    /**
     * Get enum by name
     *
     * @param name
     * @return
     */
    public static ExternalNotificationTypeEnum getByName(String name) {
        for (ExternalNotificationTypeEnum dbTypeEnum : ExternalNotificationTypeEnum.values()) {
            if (dbTypeEnum.name().equalsIgnoreCase(name)) {
                return dbTypeEnum;
            }
        }
        return null;
    }

    ExternalNotificationTypeEnum(String description, Class<? extends WebhookSender> webhookSender) {
        this.description = description;
        this.webhookSender = webhookSender;
    }
}
