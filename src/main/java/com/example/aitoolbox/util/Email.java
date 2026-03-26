package com.example.aitoolbox.util;

import cn.hutool.extra.mail.MailUtil;

public class Email {
    public String SendCodeMail(String mail, Integer Code){
        String send = MailUtil.send(mail, "ToolBox登录验证", "您的登录验证码为："+Code, false);
        return send;
    }
}
