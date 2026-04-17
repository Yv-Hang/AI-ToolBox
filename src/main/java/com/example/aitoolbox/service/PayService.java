package com.example.aitoolbox.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.aitoolbox.entity.Order;

import java.util.Map;

public interface PayService extends IService<Order> {
    /**
     * 卡密兑换积分
     * @param userId 用户ID
     * @param cardKey 卡密
     * @return 兑换结果，包含积分余额等信息
     */
    Map<String, Object> redeemCardKey(Long userId, String cardKey);

    /**
     * 购买会员
     * @param userId 用户ID
     * @param membershipId 会员ID
     * @return 购买结果，包含订单信息、支付结果、会员激活信息等
     */
    Map<String, Object> buyMembership(Long userId, Long membershipId);

    /**
     * 获取用户积分余额
     * @param userId 用户ID
     * @return 积分余额
     */
    java.math.BigDecimal getPointsBalance(Long userId);

    /**
     * 扣减用户积分（用于AI服务费用抵扣）
     * @param userId 用户ID
     * @param amount 扣减金额
     * @param remark 备注
     * @return 扣减结果
     */
    boolean deductPoints(Long userId, java.math.BigDecimal amount, String remark);

    /**
     * 新增卡密
     * @param points 积分金额
     * @param expireTime 过期时间（秒）
     * @param count 卡密数量
     * @return 生成的卡密列表
     */
    Map<String, Object> addCardKey(java.math.BigDecimal points, long expireTime, int count);

    /**
     * 查询剩余卡密
     * @param page 页码
     * @param size 每页大小
     * @return 剩余卡密列表
     */
    Map<String, Object> getRemainingCardKeys(int page, int size);
}