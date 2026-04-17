package com.example.aitoolbox.controller;

import com.example.aitoolbox.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
@Tag(name = "支付管理", description = "卡密兑换、积分支付等操作")
public class PayController {

    @Autowired
    private PayService payService;

    @Operation(summary = "卡密兑换积分", description = "使用卡密兑换积分到用户账户")
    @PostMapping("/redeem")
    public Map<String, Object> redeemCardKey(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "卡密编码", required = true) @RequestParam String cardKey) {
        return payService.redeemCardKey(userId, cardKey);
    }

    @Operation(summary = "购买会员", description = "用户购买会员，自动生成订单并支付")
    @PostMapping("/buy-membership")
    public Map<String, Object> buyMembership(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "会员ID", required = true) @RequestParam Long membershipId) {
        return payService.buyMembership(userId, membershipId);
    }

    @Operation(summary = "获取积分余额", description = "查询用户当前积分余额")
    @GetMapping("/balance")
    public Map<String, Object> getPointsBalance(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        BigDecimal balance = payService.getPointsBalance(userId);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("balance", balance);
        return result;
    }

    @Operation(summary = "扣减积分", description = "扣减用户积分（用于AI服务费用抵扣等场景）")
    @PostMapping("/deduct")
    public Map<String, Object> deductPoints(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "扣减金额", required = true) @RequestParam BigDecimal amount,
            @Parameter(description = "操作备注", required = true) @RequestParam String remark) {
        boolean success = payService.deductPoints(userId, amount, remark);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", success);
        if (success) {
            result.put("message", "积分扣减成功");
            result.put("balance", payService.getPointsBalance(userId));
        } else {
            result.put("message", "积分余额不足");
        }
        return result;
    }

    @Operation(summary = "新增卡密", description = "生成新的卡密，用于兑换积分")
    @PostMapping("/add-card-key")
    public Map<String, Object> addCardKey(
            @Parameter(description = "积分金额", required = true) @RequestParam BigDecimal points,
            @Parameter(description = "过期时间（秒）", required = true) @RequestParam long expireTime,
            @Parameter(description = "卡密数量", required = true) @RequestParam int count) {
        return payService.addCardKey(points, expireTime, count);
    }

    @Operation(summary = "查询剩余卡密", description = "查询未使用且未过期的卡密")
    @GetMapping("/remaining-card-keys")
    public Map<String, Object> getRemainingCardKeys(
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "10") int size) {
        return payService.getRemainingCardKeys(page, size);
    }
}