package com.example.aitoolbox.util;

import com.volcengine.ark.runtime.model.completion.chat.*;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DoubaoAiClient {

    @Value("${doubao.ai.api.key}")
    private String apiKey;

    private ArkService service;

    /**
     * 初始化ArkService
     */
    private void initService() {
        if (service == null) {
            ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
            Dispatcher dispatcher = new Dispatcher();
            service = ArkService.builder()
                    .dispatcher(dispatcher)
                    .connectionPool(connectionPool)
                    .apiKey(apiKey)
                    .build();
        }
    }

    /**
     * 调用豆包AI API生成文案
     * @param prompt 提示词
     * @param model 模型名称
     * @return 生成的文案内容
     * @throws Exception 异常
     */
    public String generateCopywriting(String prompt, String model) throws Exception {
        AiResponse response = generateCopywritingWithTokens(prompt, model);
        return response.getContent();
    }

    /**
     * 调用豆包AI API生成文案（返回完整响应，包含Token使用信息）
     * @param prompt 提示词
     * @param model 模型名称
     * @return AI响应结果，包含文案内容和Token使用信息
     * @throws Exception 异常
     */
    public AiResponse generateCopywritingWithTokens(String prompt, String model) throws Exception {
        initService();

        List<ChatMessage> messagesForReqList = new ArrayList<>();
        ChatMessage elementForMessagesForReqList0 = 
                ChatMessage.builder().role(ChatMessageRole.USER).content(prompt).build();
        messagesForReqList.add(elementForMessagesForReqList0);

        ChatCompletionRequest req = 
                ChatCompletionRequest.builder()
                        .model(model)
                        .messages(messagesForReqList)
                        .build();

        var completion = service.createChatCompletion(req);
        List<ChatCompletionChoice> choices = completion.getChoices();
        if (!choices.isEmpty()) {
            Object content = choices.get(0).getMessage().getContent();
            String contentStr = content != null ? content.toString() : "";

            long totalTokens = 0;
            long promptTokens = 0;
            long completionTokens = 0;

            try {
                var usage = completion.getUsage();
                if (usage != null) {
                    totalTokens = usage.getTotalTokens();
                    promptTokens = usage.getPromptTokens();
                    completionTokens = usage.getCompletionTokens();
                }
            } catch (Exception e) {
                // 记录错误但不影响文案生成
                System.err.println("Error getting usage information: " + e.getMessage());
            }

            // 如果获取不到token信息，使用默认值（1000 tokens）
            if (totalTokens == 0) {
                totalTokens = 1000;
                promptTokens = 500;
                completionTokens = 500;
                System.err.println("Using default token values since usage information is not available");
            }

            return new AiResponse(contentStr, totalTokens, promptTokens, completionTokens);
        }

        throw new Exception("Failed to generate copywriting");
    }

    /**
     * 检查API密钥是否配置
     * @return 是否配置
     */
    public boolean isApiKeyConfigured() {
        return apiKey != null && !apiKey.equals("default_value");
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        if (service != null) {
            service.shutdownExecutor();
        }
    }
}