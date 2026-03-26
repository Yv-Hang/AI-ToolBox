package com.example.aitoolbox.util;

import cn.hutool.extra.mail.MailUtil;

public class Email {
    public static void SendCodeMail(String mail, String code){
        MailUtil.send(mail, "ToolBox登录验证", "您的登录验证码为："+code, false);
    }
}
