package com.example.aitoolbox.controller;

import com.example.aitoolbox.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI服务", description = "AI服务使用和费用抵扣")
public class AiController {

    @Autowired
    private AiService aiService;





    @Operation(summary = "获取生成历史", description = "查询用户的文案生成历史记录")
    @GetMapping("/history")
    public Map<String, Object> getGenerationHistory(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "10") int size) {
        return aiService.getGenerationHistory(userId, page, size);
    }

    @Operation(summary = "获取对话详情", description = "查询单条文案生成记录的详细信息")
    @GetMapping("/history/{id}")
    public Map<String, Object> getConversationDetail(
            @Parameter(description = "记录ID", required = true) @PathVariable Long id,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        return aiService.getConversationDetail(id, userId);
    }

    @Operation(summary = "获取可用AI模型", description = "获取系统中可用的AI模型列表")
    @GetMapping("/models")
    public Map<String, Object> getAvailableModels() {
        return aiService.getAvailableModels();
    }

    @Operation(summary = "生成文案", description = "根据用户参数生成文案")
    @PostMapping("/copywriting")
    public Map<String, Object> generateCopywriting(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "文案用途/场景", required = true) @RequestParam String purpose,
            @Parameter(description = "产品/主题关键词", required = true) @RequestParam String keywords,
            @Parameter(description = "目标风格", required = true) @RequestParam String style,
            @Parameter(description = "字数", required = true) @RequestParam int wordCount,
            @Parameter(description = "生成条数", required = true) @RequestParam int count,
            @Parameter(description = "使用的模型", required = true) @RequestParam String model) {
        return aiService.generateCopywriting(userId, purpose, keywords, style, wordCount, count, model);
    }

    @Operation(summary = "新增AI模型", description = "添加新的AI模型到系统")
    @PostMapping("/models")
    public Map<String, Object> addAiModel(
            @Parameter(description = "模型名称", required = true) @RequestParam String name,
            @Parameter(description = "模型代码", required = true) @RequestParam String code,
            @Parameter(description = "模型描述", required = false) @RequestParam String description,
            @Parameter(description = "每1000 tokens的价格", required = true) @RequestParam java.math.BigDecimal pricePer1000Tokens) {
        return aiService.addAiModel(name, code, description, pricePer1000Tokens);
    }
}