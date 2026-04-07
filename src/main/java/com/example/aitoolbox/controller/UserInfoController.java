package com.example.aitoolbox.controller;

import com.example.aitoolbox.entity.AiChatHistory;
import com.example.aitoolbox.service.UserInfoService;
import com.example.aitoolbox.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "用户个人信息接口")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Operation(summary = "获取用户个人信息", description = "获取用户的基本信息、会员状态、剩余免费次数等")
    @GetMapping("/info")
    public UserInfoVO getUserInfo(
            @Parameter(name = "userId", description = "用户ID", required = true) @RequestParam Long userId) {
        return userInfoService.getUserInfo(userId);
    }

    @Operation(summary = "获取用户对话历史", description = "获取用户与AI的对话历史记录，支持分页")
    @GetMapping("/chat-history")
    public List<AiChatHistory> getChatHistory(
            @Parameter(name = "userId", description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(name = "page", description = "页码，默认1", required = false) @RequestParam(defaultValue = "1") Integer page,
            @Parameter(name = "size", description = "每页条数，默认10", required = false) @RequestParam(defaultValue = "10") Integer size) {
        return userInfoService.getChatHistory(userId, page, size);
    }

    @Operation(summary = "获取剩余免费次数", description = "获取普通用户今日剩余的免费使用次数")
    @GetMapping("/remaining-free-count")
    public Integer getRemainingFreeCount(
            @Parameter(name = "userId", description = "用户ID", required = true) @RequestParam Long userId) {
        return userInfoService.getRemainingFreeCount(userId);
    }

    @Operation(summary = "检查用户配额", description = "检查用户是否有使用AI的配额")
    @GetMapping("/check-quota")
    public boolean checkUserQuota(
            @Parameter(name = "userId", description = "用户ID", required = true) @RequestParam Long userId) {
        return userInfoService.checkUserQuota(userId);
    }

    @Operation(summary = "增加使用次数", description = "增加用户的使用次数，用于AI调用后更新")
    @PostMapping("/increment-usage")
    public void incrementUsageCount(
            @Parameter(name = "userId", description = "用户ID", required = true) @RequestParam Long userId) {
        userInfoService.incrementUsageCount(userId);
    }
}
