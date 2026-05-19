package com.example.aitoolbox.controller;

import com.example.aitoolbox.entity.R;
import com.example.aitoolbox.entity.TtsVoice;
import com.example.aitoolbox.service.TtsService;
import com.example.aitoolbox.vo.TtsSynthesizeRequest;
import com.example.aitoolbox.vo.TtsSynthesizeResponse;
import com.example.aitoolbox.vo.TtsVoiceDesignRequest;
import com.example.aitoolbox.vo.TtsHistoryPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tts")
@Tag(name = "TTS语音合成", description = "语音合成相关接口")
public class TtsController {

    @Autowired
    private TtsService ttsService;

    @Operation(summary = "获取预置音色列表", description = "获取系统支持的预置音色")
    @GetMapping("/voices")
    public R<List<TtsVoice>> getVoices() {
        return R.ok(ttsService.getPresetVoices());
    }

    @Operation(summary = "预置音色合成", description = "使用预置音色进行语音合成")
    @PostMapping("/synthesize")
    public R<TtsSynthesizeResponse> synthesize(@Valid @RequestBody TtsSynthesizeRequest request) {
        try {
            TtsSynthesizeResponse response = ttsService.synthesize(request);
            return R.ok(response);
        } catch (Exception e) {
            return R.fail(50001, "语音合成失败：" + e.getMessage());
        }
    }

    @Operation(summary = "音色设计合成", description = "通过文本描述定制音色并合成语音")
    @PostMapping("/voice-design")
    public R<TtsSynthesizeResponse> voiceDesign(@Valid @RequestBody TtsVoiceDesignRequest request) {
        try {
            TtsSynthesizeResponse response = ttsService.voiceDesign(request);
            return R.ok(response);
        } catch (Exception e) {
            return R.fail(50002, "音色设计失败：" + e.getMessage());
        }
    }

    @Operation(summary = "音色复刻合成", description = "基于音频样本复刻音色并合成语音")
    @PostMapping(value = "/voice-clone", consumes = "multipart/form-data")
    public R<TtsSynthesizeResponse> voiceClone(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "模型", required = true) @RequestParam String model,
            @Parameter(description = "文本内容", required = true) @RequestParam String text,
            @Parameter(description = "风格描述") @RequestParam(required = false) String styleInstruction,
            @Parameter(description = "音频格式") @RequestParam(required = false, defaultValue = "wav") String audioFormat,
            @Parameter(description = "音频样本文件", required = true) @RequestParam("audioSample") MultipartFile audioSample) {
        try {
            byte[] audioBytes = audioSample.getBytes();
            String fileName = audioSample.getOriginalFilename();
            TtsSynthesizeResponse response = ttsService.voiceClone(userId, model, text, styleInstruction, audioFormat, audioBytes, fileName);
            return R.ok(response);
        } catch (Exception e) {
            return R.fail(50003, "音色复刻失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取历史记录", description = "获取用户的语音合成历史记录")
    @GetMapping("/history")
    public R<TtsHistoryPageResponse> getHistory(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize) {
        TtsHistoryPageResponse response = ttsService.getHistory(userId, page, pageSize);
        return R.ok(response);
    }
}
