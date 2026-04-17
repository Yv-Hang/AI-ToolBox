package com.example.aitoolbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.aitoolbox.entity.*;
import com.example.aitoolbox.mapper.*;
import com.example.aitoolbox.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PayServiceImpl extends ServiceImpl<OrderMapper, Order> implements PayService {

    @Autowired
    private CardKeyMapper cardKeyMapper;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private MembershipInfoMapper membershipInfoMapper;

    @Autowired
    private UserMembershipMapper userMembershipMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private com.example.aitoolbox.mapper.LoginMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> redeemCardKey(Long userId, String cardKey) {
        Map<String, Object> result = new HashMap<>();

        // 1. 查找卡密
        LambdaQueryWrapper<CardKey> cardKeyQuery = new LambdaQueryWrapper<>();
        cardKeyQuery.eq(CardKey::getKeyCode, cardKey);
        CardKey card = cardKeyMapper.selectOne(cardKeyQuery);

        if (card == null) {
            result.put("success", false);
            result.put("message", "卡密不存在");
            return result;
        }

        // 2. 检查卡密状态
        if (card.getStatus() != 1) {
            result.put("success", false);
            result.put("message", "卡密已使用或已过期");
            return result;
        }

        // 3. 检查卡密是否过期
        if (card.getExpireTime() != null && card.getExpireTime().isBefore(LocalDateTime.now())) {
            card.setStatus(3); // 标记为已过期
            cardKeyMapper.updateById(card);
            result.put("success", false);
            result.put("message", "卡密已过期");
            return result;
        }

        // 4. 获取用户信息
        com.example.aitoolbox.entity.User user = userMapper.selectById(userId);

        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        // 5. 计算新的积分余额
        java.math.BigDecimal newPoints = user.getPoints().add(card.getPoints());
        user.setPoints(newPoints);
        userMapper.updateById(user);

        // 6. 更新卡密状态
        card.setStatus(2); // 标记为已使用
        card.setUserId(userId);
        card.setUsedTime(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());
        cardKeyMapper.updateById(card);

        // 7. 记录积分变动
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType(1); // 1-卡密兑换
        record.setAmount(card.getPoints());
        record.setBalance(newPoints);
        record.setRemark("卡密兑换积分：" + card.getKeyCode());
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        // 8. 返回结果
        result.put("success", true);
        result.put("message", "卡密兑换成功");
        result.put("points", card.getPoints());
        result.put("balance", newPoints);
        result.put("transactionId", UUID.randomUUID().toString());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> buyMembership(Long userId, Long membershipId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 检查会员是否存在
        MembershipInfo membership = membershipInfoMapper.selectById(membershipId);
        if (membership == null) {
            result.put("success", false);
            result.put("message", "会员套餐不存在");
            return result;
        }

        // 2. 获取用户信息
        com.example.aitoolbox.entity.User user = userMapper.selectById(userId);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        // 3. 检查积分余额
        if (user.getPoints().compareTo(membership.getPrice()) < 0) {
            result.put("success", false);
            result.put("message", "积分余额不足");
            result.put("currentBalance", user.getPoints());
            result.put("requiredAmount", membership.getPrice());
            return result;
        }

        // 4. 生成订单号（使用雪花算法）
        String orderNo = generateOrderNo();

        // 5. 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setMembershipId(membershipId);
        order.setAmount(membership.getPrice());
        order.setStatus(0); // 待支付
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.insert(order);

        // 6. 扣减积分
        java.math.BigDecimal newPoints = user.getPoints().subtract(membership.getPrice());
        user.setPoints(newPoints);
        userMapper.updateById(user);

        // 7. 更新订单状态
        order.setStatus(1); // 标记为已支付
        order.setPaymentTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        // 8. 记录积分变动
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType(2); // 2-订单支付
        record.setAmount(membership.getPrice().negate()); // 负数表示扣减
        record.setBalance(newPoints);
        record.setRemark("积分支付订单：" + orderNo);
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        // 9. 激活会员
        // 检查用户是否已有会员
        LambdaQueryWrapper<UserMembership> umQuery = new LambdaQueryWrapper<>();
        umQuery.eq(UserMembership::getUserId, userId);
        UserMembership userMembership = userMembershipMapper.selectOne(umQuery);

        LocalDateTime now = LocalDateTime.now();
        if (userMembership != null) {
            // 延长会员期限
            LocalDateTime endTime = userMembership.getEndTime().isAfter(now) ? 
                    userMembership.getEndTime().plusDays(membership.getDuration()) : 
                    now.plusDays(membership.getDuration());
            userMembership.setEndTime(endTime);
            userMembership.setStatus(1);
            userMembership.setUpdatedAt(now);
            userMembershipMapper.updateById(userMembership);
        } else {
            // 创建新的会员记录
            userMembership = new UserMembership();
            userMembership.setUserId(userId);
            userMembership.setMembershipId(membership.getId());
            userMembership.setStartTime(now);
            userMembership.setEndTime(now.plusDays(membership.getDuration()));
            userMembership.setStatus(1);
            userMembership.setCreatedAt(now);
            userMembership.setUpdatedAt(now);
            userMembershipMapper.insert(userMembership);
        }

        // 10. 返回结果
        result.put("success", true);
        result.put("message", "购买成功");
        result.put("orderNo", orderNo);
        result.put("amount", membership.getPrice());
        result.put("balance", newPoints);
        result.put("transactionId", UUID.randomUUID().toString());
        result.put("membershipActivated", true);
        result.put("membershipName", membership.getName());
        result.put("membershipEndTime", userMembership.getEndTime());

        return result;
    }

    /**
     * 生成订单号（使用雪花算法）
     * @return 订单号
     */
    private String generateOrderNo() {
        // 使用当前时间戳 + 随机数生成订单号
        // 实际项目中可以使用更复杂的雪花算法实现
        return "ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    @Override
    public java.math.BigDecimal getPointsBalance(Long userId) {
        com.example.aitoolbox.entity.User user = userMapper.selectById(userId);
        return user != null ? user.getPoints() : java.math.BigDecimal.ZERO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductPoints(Long userId, java.math.BigDecimal amount, String remark) {
        // 获取用户信息
        com.example.aitoolbox.entity.User user = userMapper.selectById(userId);

        if (user == null || user.getPoints().compareTo(amount) < 0) {
            return false;
        }

        // 扣减积分
        java.math.BigDecimal newPoints = user.getPoints().subtract(amount);
        user.setPoints(newPoints);
        userMapper.updateById(user);

        // 记录积分变动
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType(3); // 3-AI服务扣费
        record.setAmount(amount.negate()); // 负数表示扣减
        record.setBalance(newPoints);
        record.setRemark(remark);
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addCardKey(java.math.BigDecimal points, long expireTime, int count) {
        Map<String, Object> result = new HashMap<>();
        java.util.List<String> cardKeys = new java.util.ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDateTime = now.plusSeconds(expireTime);

        for (int i = 0; i < count; i++) {
            // 生成唯一卡密
            String keyCode = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
            
            // 创建卡密记录
            CardKey cardKey = new CardKey();
            cardKey.setKeyCode(keyCode);
            cardKey.setPoints(points);
            cardKey.setStatus(1); // 1-未使用
            cardKey.setExpireTime(expireDateTime);
            cardKey.setCreatedAt(now);
            cardKey.setUpdatedAt(now);
            
            // 保存到数据库
            cardKeyMapper.insert(cardKey);
            cardKeys.add(keyCode);
        }

        result.put("success", true);
        result.put("message", "卡密生成成功");
        result.put("cardKeys", cardKeys);
        result.put("count", count);
        result.put("points", points);
        result.put("expireTime", expireDateTime);

        return result;
    }

    @Override
    public Map<String, Object> getRemainingCardKeys(int page, int size) {
        Map<String, Object> result = new HashMap<>();

        // 构建查询条件：状态为1（未使用）且未过期
        LambdaQueryWrapper<CardKey> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardKey::getStatus, 1)
                .gt(CardKey::getExpireTime, LocalDateTime.now())
                .orderByDesc(CardKey::getCreatedAt);

        // 分页查询
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CardKey> pageInfo = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CardKey> cardKeysPage = cardKeyMapper.selectPage(pageInfo, queryWrapper);

        result.put("success", true);
        result.put("data", cardKeysPage.getRecords());
        result.put("total", cardKeysPage.getTotal());
        result.put("current", cardKeysPage.getCurrent());
        result.put("size", cardKeysPage.getSize());
        result.put("pages", cardKeysPage.getPages());

        return result;
    }
}