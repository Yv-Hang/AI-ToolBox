package com.example.aitoolbox.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TtsSynthesizeResponse {

    private String taskId;
    private String audioUrl;
    private String audioData;
    private BigDecimal duration;
    private Integer textLength;
    private String format;
    private Integer sampleRate;
    private String optimizedText;

    public TtsSynthesizeResponse() {
        this.audioUrl = "";
    }
}
