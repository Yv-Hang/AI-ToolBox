package com.example.aitoolbox.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TtsSynthesizeRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "模型不能为空")
    private String model;

    @NotBlank(message = "文本不能为空")
    private String text;

    private String voice = "冰糖";

    private String styleInstruction;

    private String audioFormat = "wav";
}
