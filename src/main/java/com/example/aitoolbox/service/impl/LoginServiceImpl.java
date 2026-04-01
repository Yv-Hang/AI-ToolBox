package com.example.aitoolbox.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.example.aitoolbox.entity.User;
import com.example.aitoolbox.mapper.LoginMapper;
import com.example.aitoolbox.service.LoginService;
import com.example.aitoolbox.util.Email;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    private String CodeKey = "login:code:";
    private final LoginMapper loginMapper;
    private final RedisTemplate<String, String> redisTemplate;
    
    public LoginServiceImpl(RedisTemplate<String, String> redisTemplate, LoginMapper loginMapper) {
        this.redisTemplate = redisTemplate;
        this.loginMapper = loginMapper;
    }
    
    @Override
    public void sendCode(String email) {
        String code = RandomUtil.randomNumbers(6);
        // 将验证码存储到 Redis 中，键为邮箱，值为验证码，过期时间 5 分钟
        redisTemplate.opsForValue().set(CodeKey+email, code, 5, TimeUnit.MINUTES);
        Email.SendCodeMail(email, code);
    }

    @Override
    public void register(User user) {
        String redisCode = redisTemplate.opsForValue().get(CodeKey+user.getEmail());
        if (!redisCode.equals(user.getCode())){
            throw new RuntimeException("验证码错误！");
        }
        loginMapper.add();
    }
}
