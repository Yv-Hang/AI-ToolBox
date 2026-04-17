package com.example.aitoolbox.controller;

import com.example.aitoolbox.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "用户个人信息接口")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Operation(summary = "获取用户个人信息", description = "获取用户的基本信息、会员状态、积分余额等")
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(
            @Parameter(name = "userId", description = "用户ID", required = true) @RequestParam Long userId) {
        return userInfoService.getUserInfo(userId);
    }


}
