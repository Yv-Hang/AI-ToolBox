package com.example.aitoolbox.service;

import com.example.aitoolbox.entity.TtsVoice;
import com.example.aitoolbox.vo.TtsSynthesizeRequest;
import com.example.aitoolbox.vo.TtsSynthesizeResponse;
import com.example.aitoolbox.vo.TtsVoiceDesignRequest;
import com.example.aitoolbox.vo.TtsHistoryPageResponse;

import java.util.List;

public interface TtsService {

    List<TtsVoice> getPresetVoices();

    TtsSynthesizeResponse synthesize(TtsSynthesizeRequest request) throws Exception;

    TtsSynthesizeResponse voiceDesign(TtsVoiceDesignRequest request) throws Exception;

    TtsSynthesizeResponse voiceClone(Long userId, String model, String text, String styleInstruction, String audioFormat, byte[] audioBytes, String fileName) throws Exception;

    TtsHistoryPageResponse getHistory(Long userId, int page, int pageSize);
}
