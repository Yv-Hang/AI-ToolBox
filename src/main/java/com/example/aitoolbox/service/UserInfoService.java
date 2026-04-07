package com.example.aitoolbox.service;

import com.example.aitoolbox.entity.AiChatHistory;
import com.example.aitoolbox.vo.UserInfoVO;

import java.util.List;

public interface UserInfoService {
    UserInfoVO getUserInfo(Long userId);
    List<AiChatHistory> getChatHistory(Long userId, Integer page, Integer size);
    Integer getRemainingFreeCount(Long userId);
    boolean checkUserQuota(Long userId);
    void incrementUsageCount(Long userId);
}
