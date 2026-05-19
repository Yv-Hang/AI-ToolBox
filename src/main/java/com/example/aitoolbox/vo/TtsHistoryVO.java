package com.example.aitoolbox.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TtsHistoryVO {

    private Long id;
    private String taskId;
    private String mode;
    private String text;
    private String voice;
    private String voiceDescription;
    private String styleInstruction;
    private String audioFormat;
    private BigDecimal duration;
    private Integer textLength;
    private Integer status;
    private LocalDateTime createdAt;
}
