package com.example.aitoolbox.service.impl;

import cn.hutool.core.codec.Base64;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aitoolbox.entity.TtsHistory;
import com.example.aitoolbox.entity.TtsVoice;
import com.example.aitoolbox.mapper.TtsHistoryMapper;
import com.example.aitoolbox.service.TtsService;
import com.example.aitoolbox.util.MimoAiClient;
import com.example.aitoolbox.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TtsServiceImpl implements TtsService {

    @Autowired
    private MimoAiClient mimoAiClient;

    @Autowired
    private TtsHistoryMapper ttsHistoryMapper;

    private static final List<TtsVoice> PRESET_VOICES = Arrays.asList(
            new TtsVoice("冰糖", "冰糖", "中文", "female", "通用场景、有声读物"),
            new TtsVoice("茉莉", "茉莉", "中文", "female", "温柔、治愈风格"),
            new TtsVoice("苏打", "苏打", "中文", "male", "正式、播报风格"),
            new TtsVoice("白桦", "白桦", "中文", "male", "沉稳、磁性风格"),
            new TtsVoice("Mia", "Mia", "英文", "female", "英文通用场景"),
            new TtsVoice("Chloe", "Chloe", "英文", "female", "活泼、欢快风格"),
            new TtsVoice("Milo", "Milo", "英文", "male", "英文男声"),
            new TtsVoice("Dean", "Dean", "英文", "male", "深沉、专业风格")
    );

    private static final Set<String> VALID_AUDIO_FORMATS = Set.of("wav", "mp3", "pcm16");

    @Override
    public List<TtsVoice> getPresetVoices() {
        return PRESET_VOICES;
    }

    @Override
    public TtsSynthesizeResponse synthesize(TtsSynthesizeRequest request) throws Exception {
        String taskId = generateTaskId("tts");
        TtsSynthesizeResponse response = new TtsSynthesizeResponse();
        response.setTaskId(taskId);

        try {
            Map<String, Object> apiResponse = mimoAiClient.synthesizeWithPresetVoice(
                    request.getText(),
                    request.getVoice(),
                    request.getStyleInstruction(),
                    request.getAudioFormat()
            );

            buildResponse(response, apiResponse, request.getAudioFormat(), request.getText().length());
            saveHistory(request.getUserId(), request.getModel(), "preset", request.getText(),
                    request.getVoice(), null, request.getStyleInstruction(), request.getAudioFormat(),
                    (BigDecimal) apiResponse.get("duration"), request.getText().length(),
                    (Integer) apiResponse.get("sampleRate"),
                    apiResponse.get("totalTokens") != null ? ((Number) apiResponse.get("totalTokens")).intValue() : 0,
                    BigDecimal.ZERO, 1, null, taskId);

        } catch (Exception e) {
            saveHistory(request.getUserId(), request.getModel(), "preset", request.getText(),
                    request.getVoice(), null, request.getStyleInstruction(), request.getAudioFormat(),
                    null, request.getText().length(), null, 0, BigDecimal.ZERO, 0, e.getMessage(), taskId);
            throw e;
        }

        return response;
    }

    @Override
    public TtsSynthesizeResponse voiceDesign(TtsVoiceDesignRequest request) throws Exception {
        String taskId = generateTaskId("tts_vd");
        TtsSynthesizeResponse response = new TtsSynthesizeResponse();
        response.setTaskId(taskId);

        try {
            Map<String, Object> apiResponse = mimoAiClient.synthesizeWithVoiceDesign(
                    request.getVoiceDescription(),
                    request.getText(),
                    request.getOptimizeTextPreview(),
                    request.getAudioFormat()
            );

            buildResponse(response, apiResponse, request.getAudioFormat(),
                    request.getText() != null ? request.getText().length() : 0);
            response.setOptimizedText((String) apiResponse.get("optimizedText"));

            saveHistory(request.getUserId(), request.getModel(), "design",
                    request.getText() != null ? request.getText() : "", null,
                    request.getVoiceDescription(), null, request.getAudioFormat(),
                    (BigDecimal) apiResponse.get("duration"),
                    request.getText() != null ? request.getText().length() : 0,
                    (Integer) apiResponse.get("sampleRate"),
                    apiResponse.get("totalTokens") != null ? ((Number) apiResponse.get("totalTokens")).intValue() : 0,
                    BigDecimal.ZERO, 1, null, taskId);

        } catch (Exception e) {
            saveHistory(request.getUserId(), request.getModel(), "design",
                    request.getText() != null ? request.getText() : "", null,
                    request.getVoiceDescription(), null, request.getAudioFormat(),
                    null, request.getText() != null ? request.getText().length() : 0,
                    null, 0, BigDecimal.ZERO, 0, e.getMessage(), taskId);
            throw e;
        }

        return response;
    }

    @Override
    public TtsSynthesizeResponse voiceClone(Long userId, String model, String text, String styleInstruction,
                                            String audioFormat, byte[] audioBytes, String fileName) throws Exception {
        String taskId = generateTaskId("tts_vc");
        TtsSynthesizeResponse response = new TtsSynthesizeResponse();
        response.setTaskId(taskId);

        String audioBase64 = Base64.encode(audioBytes);
        String mimeType = getMimeType(fileName);

        try {
            Map<String, Object> apiResponse = mimoAiClient.synthesizeWithVoiceClone(
                    audioBase64, mimeType, text, styleInstruction, audioFormat
            );

            buildResponse(response, apiResponse, audioFormat, text.length());
            saveHistory(userId, model, "clone", text, null, null, styleInstruction, audioFormat,
                    (BigDecimal) apiResponse.get("duration"), text.length(),
                    (Integer) apiResponse.get("sampleRate"),
                    apiResponse.get("totalTokens") != null ? ((Number) apiResponse.get("totalTokens")).intValue() : 0,
                    BigDecimal.ZERO, 1, null, taskId);

        } catch (Exception e) {
            saveHistory(userId, model, "clone", text, null, null, styleInstruction, audioFormat,
                    null, text.length(), null, 0, BigDecimal.ZERO, 0, e.getMessage(), taskId);
            throw e;
        }

        return response;
    }

    @Override
    public TtsHistoryPageResponse getHistory(Long userId, int page, int pageSize) {
        TtsHistoryPageResponse response = new TtsHistoryPageResponse();

        LambdaQueryWrapper<TtsHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TtsHistory::getUserId, userId)
                .orderByDesc(TtsHistory::getCreatedAt);

        Page<TtsHistory> pageInfo = new Page<>(page, pageSize);
        Page<TtsHistory> historyPage = ttsHistoryMapper.selectPage(pageInfo, queryWrapper);

        List<TtsHistoryVO> historyVOList = new ArrayList<>();
        for (TtsHistory history : historyPage.getRecords()) {
            TtsHistoryVO vo = new TtsHistoryVO();
            BeanUtils.copyProperties(history, vo);
            historyVOList.add(vo);
        }

        response.setTotal(historyPage.getTotal());
        response.setPage(page);
        response.setPageSize(pageSize);
        response.setList(historyVOList);

        return response;
    }

    private String generateTaskId(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6);
    }

    private void buildResponse(TtsSynthesizeResponse response, Map<String, Object> apiResponse, String format, int textLength) {
        response.setAudioData((String) apiResponse.get("audioData"));
        response.setDuration((BigDecimal) apiResponse.get("duration"));
        response.setTextLength(textLength);
        response.setFormat(format);
        response.setSampleRate((Integer) apiResponse.get("sampleRate"));
    }

    private void saveHistory(Long userId, String model, String mode, String text, String voice,
                             String voiceDescription, String styleInstruction, String audioFormat,
                             BigDecimal duration, Integer textLength, Integer sampleRate,
                             Integer tokensConsumed, BigDecimal pointsDeducted, Integer status,
                             String errorMessage, String taskId) {
        TtsHistory history = new TtsHistory();
        history.setUserId(userId);
        history.setModel(model);
        history.setMode(mode);
        history.setText(text);
        history.setVoice(voice);
        history.setVoiceDescription(voiceDescription);
        history.setStyleInstruction(styleInstruction);
        history.setAudioFormat(audioFormat);
        history.setDuration(duration);
        history.setTextLength(textLength);
        history.setSampleRate(sampleRate);
        history.setTokensConsumed(tokensConsumed);
        history.setPointsDeducted(pointsDeducted);
        history.setStatus(status);
        history.setErrorMessage(errorMessage);
        history.setTaskId(taskId);
        history.setCreatedAt(LocalDateTime.now());
        ttsHistoryMapper.insert(history);
    }

    private String getMimeType(String fileName) {
        if (fileName.toLowerCase().endsWith(".mp3")) {
            return "audio/mpeg";
        }
        return "audio/wav";
    }
}
