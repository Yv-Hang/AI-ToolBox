package com.example.aitoolbox.service;

import java.util.Map;

public interface AiService {
    /**
     * 根据模型和token数计算费用
     * @param model AI模型名称
     * @param tokens token数
     * @return 费用
     */
    java.math.BigDecimal calculateCost(String model, long tokens);

    /**
     * 获取生成历史
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 历史记录列表
     */
    Map<String, Object> getGenerationHistory(Long userId, int page, int size);

    /**
     * 获取对话详情
     * @param id 记录ID
     * @param userId 用户ID
     * @return 详细记录
     */
    Map<String, Object> getConversationDetail(Long id, Long userId);

    /**
     * 获取可用的AI模型列表
     * @return 可用的AI模型列表
     */
    Map<String, Object> getAvailableModels();

    /**
     * 生成文案
     * @param userId 用户ID
     * @param purpose 文案用途/场景
     * @param keywords 产品/主题关键词
     * @param style 目标风格
     * @param wordCount 字数
     * @param count 生成条数
     * @param model 使用的模型
     * @return 生成结果
     */
    Map<String, Object> generateCopywriting(Long userId, String purpose, String keywords, String style, int wordCount, int count, String model);

    /**
     * 新增AI模型
     * @param name 模型名称
     * @param code 模型代码
     * @param description 模型描述
     * @param pricePer1000Tokens 每1000 tokens的价格
     * @return 新增结果
     */
    Map<String, Object> addAiModel(String name, String code, String description, java.math.BigDecimal pricePer1000Tokens);
}