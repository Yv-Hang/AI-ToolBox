package com.example.aitoolbox.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiResponse {
    private String content;
    private long totalTokens;
    private long promptTokens;
    private long completionTokens;
}