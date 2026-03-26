package com.example.aitoolbox.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.example.aitoolbox.service.LoginService;
import com.example.aitoolbox.util.Email;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Override
    public void sendCode(String email) {
        Email.SendCodeMail(email, RandomUtil.randomNumbers(6));
    }
}
