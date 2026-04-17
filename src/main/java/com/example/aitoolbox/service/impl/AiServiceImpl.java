package com.example.aitoolbox.service.impl;

import com.example.aitoolbox.service.AiService;
import com.example.aitoolbox.service.PayService;
import com.example.aitoolbox.util.DoubaoAiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private PayService payService;

    @Autowired
    private DoubaoAiClient doubaoAiClient;

    @Autowired
    private com.example.aitoolbox.mapper.XiaohongshuCopywritingMapper xiaohongshuCopywritingMapper;

    @Autowired
    private com.example.aitoolbox.mapper.AiModelMapper aiModelMapper;

    // 不同AI模型的token价格（元/1000 tokens）
    private static final Map<String, java.math.BigDecimal> MODEL_PRICES = new HashMap<>();

    static {
        // 初始化模型价格
        MODEL_PRICES.put("gpt-3.5-turbo", new java.math.BigDecimal("0.0015"));
        MODEL_PRICES.put("gpt-4", new java.math.BigDecimal("0.03"));
        MODEL_PRICES.put("gpt-4o", new java.math.BigDecimal("0.005"));
        MODEL_PRICES.put("doubao", new java.math.BigDecimal("0.002"));
        // 可以根据需要添加更多模型的价格
    }



    @Override
    public java.math.BigDecimal calculateCost(String model, long tokens) {
        java.math.BigDecimal pricePer1000Tokens = MODEL_PRICES.getOrDefault(model, new java.math.BigDecimal("0.0015"));
        return pricePer1000Tokens.multiply(new java.math.BigDecimal(tokens)).divide(new java.math.BigDecimal(1000), 4, java.math.BigDecimal.ROUND_HALF_UP);
    }



    @Override
    public Map<String, Object> getGenerationHistory(Long userId, int page, int size) {
        Map<String, Object> result = new HashMap<>();

        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.example.aitoolbox.entity.XiaohongshuCopywriting> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(com.example.aitoolbox.entity.XiaohongshuCopywriting::getUserId, userId)
                .orderByDesc(com.example.aitoolbox.entity.XiaohongshuCopywriting::getCreatedAt);

        // 分页查询
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.example.aitoolbox.entity.XiaohongshuCopywriting> pageInfo = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.example.aitoolbox.entity.XiaohongshuCopywriting> copywritingPage = xiaohongshuCopywritingMapper.selectPage(pageInfo, queryWrapper);

        result.put("success", true);
        result.put("data", copywritingPage.getRecords());
        result.put("total", copywritingPage.getTotal());
        result.put("current", copywritingPage.getCurrent());
        result.put("size", copywritingPage.getSize());
        result.put("pages", copywritingPage.getPages());

        return result;
    }

    @Override
    public Map<String, Object> getConversationDetail(Long id, Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 查询记录
        com.example.aitoolbox.entity.XiaohongshuCopywriting record = xiaohongshuCopywritingMapper.selectById(id);

        if (record == null) {
            result.put("success", false);
            result.put("message", "记录不存在");
            return result;
        }

        // 验证用户权限
        if (!record.getUserId().equals(userId)) {
            result.put("success", false);
            result.put("message", "无权访问此记录");
            return result;
        }

        result.put("success", true);
        result.put("data", record);

        return result;
    }



    @Override
    public Map<String, Object> getAvailableModels() {
        Map<String, Object> result = new HashMap<>();

        // 构建查询条件：状态为1（启用）
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.example.aitoolbox.entity.AiModel> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(com.example.aitoolbox.entity.AiModel::getStatus, 1)
                .orderByAsc(com.example.aitoolbox.entity.AiModel::getId);

        // 查询所有可用的AI模型
        java.util.List<com.example.aitoolbox.entity.AiModel> models = aiModelMapper.selectList(queryWrapper);

        result.put("success", true);
        result.put("data", models);

        return result;
    }

    @Override
    public Map<String, Object> generateCopywriting(Long userId, String purpose, String keywords, String style, int wordCount, int count, String model) {
        Map<String, Object> result = new HashMap<>();

        // 1. 构建提示词模板
        String prompt = "你是专业文案助手，请根据以下信息生成文案：\n" +
                "【用途】：" + purpose + "\n" +
                "【关键词】：" + keywords + "\n" +
                "【风格】：" + style + "\n" +
                "【要求】：\n" +
                "- 字数：" + wordCount + "\n" +
                "- 生成条数：" + count + "\n" +
                "- 语言自然、不生硬、无AI感\n" +
                "- 每条独立，带序号\n" +
                "\n" +
                "开始生成：";

        // 2. 保存生成记录（初始状态）
        com.example.aitoolbox.entity.XiaohongshuCopywriting record = new com.example.aitoolbox.entity.XiaohongshuCopywriting();
        record.setUserId(userId);
        record.setOriginalPrompt(prompt);
        record.setOptimizedPrompt(prompt);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        // 3. 调用豆包AI API生成文案，获取实际Token消耗
        try {
            com.example.aitoolbox.util.AiResponse aiResponse = doubaoAiClient.generateCopywritingWithTokens(prompt, model);
            String generatedContent = aiResponse.getContent();
            long actualTokens = aiResponse.getTotalTokens();

            // 4. 直接使用总Token数作为扣减的积分数量
            java.math.BigDecimal actualCost = new java.math.BigDecimal(actualTokens);

            // 5. 扣减积分
            boolean deductSuccess = payService.deductPoints(userId, actualCost, "AI服务费用：" + model + " - " + actualTokens + " tokens");
            
            // 记录积分扣减结果
            System.out.println("User: " + userId + ", Model: " + model + ", Tokens: " + actualTokens + ", Points Deducted: " + actualCost + ", Deduct Success: " + deductSuccess);

            if (deductSuccess) {
                record.setGeneratedContent(generatedContent);
                record.setStatus(1); // 成功
                record.setTotalTokens(actualTokens);
                record.setPromptTokens(aiResponse.getPromptTokens());
                record.setCompletionTokens(aiResponse.getCompletionTokens());

                result.put("success", true);
                result.put("message", "文案生成成功");
                result.put("content", generatedContent);
                result.put("prompt", prompt);
                result.put("tokens", actualTokens);
                result.put("promptTokens", aiResponse.getPromptTokens());
                result.put("completionTokens", aiResponse.getCompletionTokens());
                result.put("cost", actualCost);
                result.put("balance", payService.getPointsBalance(userId));
            } else {
                record.setGeneratedContent(generatedContent);
                record.setStatus(0); // 失败
                record.setErrorMessage("积分余额不足");
                record.setTotalTokens(actualTokens);
                record.setPromptTokens(aiResponse.getPromptTokens());
                record.setCompletionTokens(aiResponse.getCompletionTokens());

                result.put("success", false);
                result.put("message", "文案生成成功但积分余额不足");
                result.put("content", generatedContent);
                result.put("error", "积分余额不足");
                result.put("requiredCost", actualCost);
                result.put("currentBalance", payService.getPointsBalance(userId));
            }
        } catch (Exception e) {
            record.setStatus(0); // 失败
            record.setErrorMessage(e.getMessage());

            result.put("success", false);
            result.put("message", "文案生成失败");
            result.put("error", e.getMessage());
        }

        // 6. 保存记录
        xiaohongshuCopywritingMapper.insert(record);

        return result;
    }

    @Override
    public Map<String, Object> addAiModel(String name, String code, String description, java.math.BigDecimal pricePer1000Tokens) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查模型代码是否已存在
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.example.aitoolbox.entity.AiModel> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            queryWrapper.eq(com.example.aitoolbox.entity.AiModel::getCode, code);
            com.example.aitoolbox.entity.AiModel existingModel = aiModelMapper.selectOne(queryWrapper);

            if (existingModel != null) {
                result.put("success", false);
                result.put("message", "模型代码已存在");
                return result;
            }

            // 创建新模型
            com.example.aitoolbox.entity.AiModel newModel = new com.example.aitoolbox.entity.AiModel();
            newModel.setName(name);
            newModel.setCode(code);
            newModel.setDescription(description);
            newModel.setPricePer1000Tokens(pricePer1000Tokens);
            newModel.setStatus(1); // 默认为启用状态
            newModel.setCreatedAt(LocalDateTime.now());
            newModel.setUpdatedAt(LocalDateTime.now());

            // 保存到数据库
            aiModelMapper.insert(newModel);

            result.put("success", true);
            result.put("message", "模型添加成功");
            result.put("model", newModel);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "模型添加失败");
            result.put("error", e.getMessage());
        }

        return result;
    }
}