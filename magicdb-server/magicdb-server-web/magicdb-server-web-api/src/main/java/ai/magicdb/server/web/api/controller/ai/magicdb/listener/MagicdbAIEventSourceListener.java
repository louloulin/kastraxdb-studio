package ai.magicdb.server.web.api.controller.ai.magicdb.listener;

import ai.magicdb.server.domain.api.enums.AiSqlSourceEnum;
import ai.magicdb.server.domain.api.model.Config;
import ai.magicdb.server.domain.api.service.ConfigService;
import ai.magicdb.server.tools.base.wrapper.result.DataResult;
import ai.magicdb.server.web.api.controller.ai.baichuan.model.BaichuanChatCompletions;
import ai.magicdb.server.web.api.controller.ai.baichuan.model.BaichuanChatMessage;
import ai.magicdb.server.web.api.controller.ai.magicdb.client.MagicdbAIClient;
import ai.magicdb.server.web.api.controller.ai.fastchat.model.FastChatMessage;
import ai.magicdb.server.web.api.controller.ai.response.ChatCompletionResponse;
import ai.magicdb.server.web.api.controller.ai.zhipu.model.ZhipuChatCompletions;
import ai.magicdb.server.web.api.util.ApplicationContextUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

/**
 * Description: MagicdbAIEventSourceListener
 *
 * @author https:www.unfbx.com
 * @date 2023-02-22
 */
@Slf4j
public class MagicdbAIEventSourceListener extends EventSourceListener {

    private SseEmitter sseEmitter;

    public MagicdbAIEventSourceListener(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("Magicdb AI 建立sse连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("Magicdb AI returns data: {}", data);
        if (data.equals("[DONE]")) {
            log.info("Magicdb AI return data is over");
            sseEmitter.send(SseEmitter.event()
                .id("[DONE]")
                .data("[DONE]")
                .reconnectTime(3000));
            sseEmitter.complete();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
        String text = completionResponse.getChoices().get(0).getDelta() == null
                ? completionResponse.getChoices().get(0).getText()
                : completionResponse.getChoices().get(0).getDelta().getContent();
        String completionId = completionResponse.getId();

        Message message = new Message();
        if (text != null) {
            message.setContent(text);
            sseEmitter.send(SseEmitter.event()
                .id(completionId)
                .data(message)
                .reconnectTime(3000));
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        sseEmitter.complete();
        log.info("Magicdb AI closes sse connection...");
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        try {
            if (Objects.isNull(response)) {
                String message = t.getMessage();
                Message sseMessage = new Message();
                sseMessage.setContent(message);
                sseEmitter.send(SseEmitter.event()
                    .id("[ERROR]")
                    .data(sseMessage));
                sseEmitter.send(SseEmitter.event()
                    .id("[DONE]")
                    .data("[DONE]"));
                sseEmitter.complete();
                return;
            }
            ResponseBody body = response.body();
            String bodyString = null;
            if (Objects.nonNull(body)) {
                bodyString = body.string();
                log.error("Magicdb AI sse connection exception data: {}", bodyString, t);
            } else {
                log.error("Magicdb AI sse connection exception data: {}", response, t);
            }
            eventSource.cancel();
            Message message = new Message();
            message.setContent("Magicdb AI Error：" + bodyString);
            sseEmitter.send(SseEmitter.event()
                .id("[ERROR]")
                .data(message));
            sseEmitter.send(SseEmitter.event()
                .id("[DONE]")
                .data("[DONE]"));
            sseEmitter.complete();
        } catch (Exception exception) {
            log.error("Exception in sending data:", exception);
        }
    }
}
