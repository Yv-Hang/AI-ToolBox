package com.example.aitoolbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aitoolbox.entity.*;
import com.example.aitoolbox.mapper.*;
import com.example.aitoolbox.service.UserInfoService;
import com.example.aitoolbox.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private static final int DAILY_FREE_LIMIT = 5;

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private UserMembershipMapper userMembershipMapper;

    @Autowired
    private MembershipInfoMapper membershipInfoMapper;

    @Autowired
    private AiChatHistoryMapper aiChatHistoryMapper;

    @Autowired
    private UserDailyUsageMapper userDailyUsageMapper;

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        User user = loginMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setEmail(user.getEmail());

        // 检查用户会员状态
        LambdaQueryWrapper<UserMembership> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMembership::getUserId, userId);
        UserMembership userMembership = userMembershipMapper.selectOne(wrapper);

        if (userMembership != null && userMembership.getStatus() == 1 && userMembership.getEndTime().isAfter(LocalDateTime.now())) {
            // 用户是会员
            userInfoVO.setIsMember(true);
            MembershipInfo membershipInfo = membershipInfoMapper.selectById(userMembership.getMembershipId());
            if (membershipInfo != null) {
                userInfoVO.setMembershipLevel(membershipInfo.getName());
            }
            userInfoVO.setMembershipExpireTime(userMembership.getEndTime());
            userInfoVO.setRemainingFreeCount(-1); // 会员无限制
        } else {
            // 普通用户
            userInfoVO.setIsMember(false);
            userInfoVO.setMembershipLevel("普通用户");
            userInfoVO.setMembershipExpireTime(null);
            userInfoVO.setRemainingFreeCount(getRemainingFreeCount(userId));
        }

        return userInfoVO;
    }

    @Override
    public List<AiChatHistory> getChatHistory(Long userId, Integer page, Integer size) {
        Page<AiChatHistory> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<AiChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatHistory::getUserId, userId)
                .orderByDesc(AiChatHistory::getCreatedAt);
        Page<AiChatHistory> result = aiChatHistoryMapper.selectPage(pageObj, wrapper);
        return result.getRecords();
    }

    @Override
    public Integer getRemainingFreeCount(Long userId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<UserDailyUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDailyUsage::getUserId, userId)
                .eq(UserDailyUsage::getDate, today);
        UserDailyUsage usage = userDailyUsageMapper.selectOne(wrapper);

        if (usage == null) {
            return DAILY_FREE_LIMIT;
        } else {
            return Math.max(0, DAILY_FREE_LIMIT - usage.getUsageCount());
        }
    }

    @Override
    public boolean checkUserQuota(Long userId) {
        // 检查用户是否是会员
        LambdaQueryWrapper<UserMembership> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMembership::getUserId, userId);
        UserMembership userMembership = userMembershipMapper.selectOne(wrapper);

        if (userMembership != null && userMembership.getStatus() == 1 && userMembership.getEndTime().isAfter(LocalDateTime.now())) {
            // 会员用户，无限制
            return true;
        }

        // 普通用户，检查每日使用次数
        int remainingCount = getRemainingFreeCount(userId);
        return remainingCount > 0;
    }

    @Override
    public void incrementUsageCount(Long userId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<UserDailyUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDailyUsage::getUserId, userId)
                .eq(UserDailyUsage::getDate, today);
        UserDailyUsage usage = userDailyUsageMapper.selectOne(wrapper);

        if (usage == null) {
            // 今日首次使用，创建记录
            usage = new UserDailyUsage();
            usage.setUserId(userId);
            usage.setDate(today);
            usage.setUsageCount(1);
            userDailyUsageMapper.insert(usage);
        } else {
            // 更新使用次数
            LambdaUpdateWrapper<UserDailyUsage> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserDailyUsage::getId, usage.getId())
                    .set(UserDailyUsage::getUsageCount, usage.getUsageCount() + 1);
            userDailyUsageMapper.update(null, updateWrapper);
        }
    }
}
