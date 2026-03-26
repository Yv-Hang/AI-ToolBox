package com.example.aitoolbox.controller;

import com.example.aitoolbox.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 发送验证码
     * @param email 用户邮箱
     * @return 验证码
     */
    @PostMapping( "/sendCode")
    public String sendCode(@RequestParam String email){
        try{
            loginService.sendCode(email);
        }catch (Exception e){
            e.printStackTrace();
            return "发送失败";
        }
        return "发送成功";
    }


}
