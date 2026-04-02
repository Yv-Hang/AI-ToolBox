package com.example.aitoolbox.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.example.aitoolbox.entity.User;
import com.example.aitoolbox.mapper.LoginMapper;
import com.example.aitoolbox.service.LoginService;
import com.example.aitoolbox.util.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    private String RegisterCodeKey = "register:code:";
    private String LoginCodeKey = "login:code:";
    private final LoginMapper loginMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final Snowflake snowflake;
    
    public LoginServiceImpl(RedisTemplate<String, String> redisTemplate, LoginMapper loginMapper, 
                           @Value("${snowflake.datacenter-id}") long datacenterId, 
                           @Value("${snowflake.machine-id}") long machineId) {
        this.redisTemplate = redisTemplate;
        this.loginMapper = loginMapper;
        // 初始化雪花算法，参数1为数据中心ID，参数2为机器ID
        this.snowflake = IdUtil.getSnowflake(datacenterId, machineId);
    }
    
    @Override
    public void sendRegisterCode(String email) {
        String code = RandomUtil.randomNumbers(6);
        // 将验证码存储到 Redis 中，键为邮箱，值为验证码，过期时间 5 分钟
        redisTemplate.opsForValue().set(RegisterCodeKey+email, code, 5, TimeUnit.MINUTES);
        Email.SendRegisterCodeMail(email, code);
    }
    
    @Override
    public void sendLoginCode(String email) {
        String code = RandomUtil.randomNumbers(6);
        // 将验证码存储到 Redis 中，键为邮箱，值为验证码，过期时间 5 分钟
        redisTemplate.opsForValue().set(LoginCodeKey+email, code, 5, TimeUnit.MINUTES);
        Email.SendLoginCodeMail(email, code);
    }

    @Override
    public void register(User user) {
        String redisCode = redisTemplate.opsForValue().get(RegisterCodeKey+user.getEmail());
        if (!redisCode.equals(user.getCode())){
            throw new RuntimeException("验证码错误！");
        }
        
        // 检查邮箱是否已存在
        if (loginMapper.getUserByEmail(user.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册！");
        }
        
        // 检查用户名是否已存在
        if (loginMapper.getUserByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已被使用！");
        }
        
        // 生成雪花算法ID
        user.setId(snowflake.nextId());
        // 加密密码
        user.setPassword(SecureUtil.md5(user.getPassword()));
        // 清除验证码
        user.setCode(null);
        // 设置默认状态
        user.setStatus(1);
        
        // 保存用户信息到数据库
        loginMapper.addUser(user);
    }
    
    @Override
    public User login(String usernameOrEmail, String password, String code) {
        User user = null;
        
        // 尝试通过邮箱登录
        if (usernameOrEmail.contains("@")) {
            user = loginMapper.getUserByEmail(usernameOrEmail);
            // 邮箱登录需要验证验证码
            if (user != null) {
                String redisCode = redisTemplate.opsForValue().get(LoginCodeKey+usernameOrEmail);
                if (redisCode == null || !redisCode.equals(code)) {
                    throw new RuntimeException("验证码错误！");
                }
            }
        } else {
            // 尝试通过用户名登录，不需要验证码
            user = loginMapper.getUserByUsername(usernameOrEmail);
        }
        
        if (user == null) {
            throw new RuntimeException("用户不存在！");
        }
        
        if (!user.getPassword().equals(SecureUtil.md5(password))) {
            throw new RuntimeException("密码错误！");
        }
        
        return user;
    }
}
