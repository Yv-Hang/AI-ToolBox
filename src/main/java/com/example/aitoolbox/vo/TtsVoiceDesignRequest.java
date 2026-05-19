package com.example.aitoolbox.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TtsVoiceDesignRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "模型不能为空")
    private String model;

    @NotBlank(message = "音色描述不能为空")
    private String voiceDescription;

    private String text;

    private Boolean optimizeTextPreview = true;

    private String audioFormat = "wav";
}
