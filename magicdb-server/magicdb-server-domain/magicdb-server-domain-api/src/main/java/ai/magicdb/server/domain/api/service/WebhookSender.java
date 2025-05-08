package ai.magicdb.server.domain.api.service;

import ai.magicdb.server.domain.api.param.message.MessageCreateParam;

/**
 * @author Juechen
 * @version : WebhookSender.java
 */
public interface WebhookSender {

    void sendMessage(MessageCreateParam param);

}
