package com.example.aitoolbox.controller;

import com.example.aitoolbox.entity.R;
import com.example.aitoolbox.entity.User;
import com.example.aitoolbox.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "用户认证", description = "用户注册、登录相关接口")
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Operation(summary = "用户注册", description = "用户注册接口，需要提供用户名、邮箱、密码和验证码")
    @PostMapping("/reg")
    public R<Object> register(@Parameter(description = "用户信息，包含用户名、邮箱、密码和验证码") @RequestBody User user){
        try {
            loginService.register(user);
        }catch (Exception e){
            return R.fail(e.getMessage());
        }
        return R.ok("注册成功！");
    }

    @Operation(summary = "发送注册验证码", description = "向用户邮箱发送注册验证码")
    @PostMapping( "/sendRegisterCode")
    public R<Object> sendRegisterCode(@Parameter(description = "用户邮箱") @RequestParam String email){
        try{
            loginService.sendRegisterCode(email);
        }catch (Exception e){
            e.printStackTrace();
            return R.fail("邮箱错误！");
        }
        return R.ok("发送成功！");
    }
    
    @Operation(summary = "发送登录验证码", description = "向用户邮箱发送登录验证码")
    @PostMapping( "/sendLoginCode")
    public R<Object> sendLoginCode(@Parameter(description = "用户邮箱") @RequestParam String email){
        try{
            loginService.sendLoginCode(email);
        }catch (Exception e){
            e.printStackTrace();
            return R.fail("邮箱错误！");
        }
        return R.ok("发送成功！");
    }
    
    @Operation(summary = "用户登录", description = "用户登录接口，支持用户名或邮箱登录，邮箱登录需要验证码")
    @PostMapping("/login")
    public R<Object> login(
            @Parameter(description = "用户名或邮箱") @RequestParam String usernameOrEmail, 
            @Parameter(description = "密码") @RequestParam String password, 
            @Parameter(description = "验证码（邮箱登录时必填，用户名登录时可选）") @RequestParam(required = false) String code){
        try {
            User user = loginService.login(usernameOrEmail, password, code);
            // 清除敏感信息
            user.setPassword(null);
            user.setCode(null);
            return R.ok(user);
        }catch (Exception e){
            return R.fail(e.getMessage());
        }
    }



}
