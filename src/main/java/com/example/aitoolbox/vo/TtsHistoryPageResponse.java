package com.example.aitoolbox.vo;

import lombok.Data;

import java.util.List;

@Data
public class TtsHistoryPageResponse {

    private Long total;
    private Integer page;
    private Integer pageSize;
    private List<TtsHistoryVO> list;
}
