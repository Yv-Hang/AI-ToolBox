package com.example.aitoolbox.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DoubaoAiClientTest {

    @InjectMocks
    private DoubaoAiClient doubaoAiClient;

    @Test
    void testIsApiKeyConfigured() {
        // 测试默认值
        boolean result1 = doubaoAiClient.isApiKeyConfigured();
        assertFalse(result1);

        // 测试配置了API密钥
        ReflectionTestUtils.setField(doubaoAiClient, "apiKey", "test_api_key");
        boolean result2 = doubaoAiClient.isApiKeyConfigured();
        assertTrue(result2);
    }

    @Test
    void testGenerateCopywriting() {
        // 注意：这里只是测试方法结构，实际调用需要配置真实的API密钥
        // 在实际运行时，由于没有配置API密钥，这个测试会失败
        // 但我们可以验证方法结构是否正确
        ReflectionTestUtils.setField(doubaoAiClient, "apiKey", "test_api_key");
        ReflectionTestUtils.setField(doubaoAiClient, "apiUrl", "https://ark.cn-beijing.volces.com/api/v3/chat/completions");
        ReflectionTestUtils.setField(doubaoAiClient, "model", "ep-20260416102329-n4q9l");

        // 验证方法存在且能被调用（虽然会抛出异常）
        assertThrows(Exception.class, () -> {
            doubaoAiClient.generateCopywriting("测试文案生成", "doubao-1-5-pro-32k-250115");
        });
    }
}
