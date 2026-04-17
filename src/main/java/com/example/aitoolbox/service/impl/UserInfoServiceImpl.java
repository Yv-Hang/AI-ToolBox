package com.example.aitoolbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aitoolbox.entity.User;
import com.example.aitoolbox.entity.UserMembership;
import com.example.aitoolbox.entity.MembershipInfo;
import com.example.aitoolbox.mapper.*;
import com.example.aitoolbox.service.UserInfoService;
import com.example.aitoolbox.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private UserMembershipMapper userMembershipMapper;

    @Autowired
    private MembershipInfoMapper membershipInfoMapper;



    @Override
    public java.util.Map<String, Object> getUserInfo(Long userId) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        User user = loginMapper.selectById(userId);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        com.example.aitoolbox.vo.UserInfoVO userInfoVO = new com.example.aitoolbox.vo.UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setEmail(user.getEmail());
        userInfoVO.setPoints(user.getPoints());

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
        } else {
            // 普通用户
            userInfoVO.setIsMember(false);
            userInfoVO.setMembershipLevel("普通用户");
            userInfoVO.setMembershipExpireTime(null);
        }

        result.put("success", true);
        result.put("message", "获取用户信息成功");
        result.put("data", userInfoVO);
        return result;
    }


}
