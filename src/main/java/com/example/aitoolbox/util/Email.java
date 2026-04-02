package com.example.aitoolbox.util;

import cn.hutool.extra.mail.MailUtil;

public class Email {
    private static void SendCodeMail(String mail, String code, String subject){
        MailUtil.send(mail, subject, "您的验证码为："+code, false);
    }
    
    public static void SendRegisterCodeMail(String mail, String code){
        SendCodeMail(mail, code, "ToolBox注册验证");
    }
    
    public static void SendLoginCodeMail(String mail, String code){
        SendCodeMail(mail, code, "ToolBox登录验证");
    }
}
