package com.example.aitoolbox.controller;

import com.example.aitoolbox.entity.R;
import com.example.aitoolbox.entity.User;
import com.example.aitoolbox.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/reg")
    public R<Object> register(@RequestParam User user){
        try {
            loginService.register(user);
        }catch (Exception e){
            return R.fail(e.getMessage());
        }
        return R.ok();
    }

    /**
     * 发送验证码
     *
     * @param email 用户邮箱
     * @return 验证码
     */
    @PostMapping( "/sendCode")
    public R<Object> sendCode(@RequestParam String email){
        try{
            loginService.sendCode(email);
        }catch (Exception e){
            e.printStackTrace();
            return R.fail("邮箱错误！");
        }
        return R.ok("发送成功！");
    }



}
