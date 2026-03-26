package com.example.aitoolbox;

import cn.hutool.extra.mail.MailUtil;
import com.example.aitoolbox.util.Email;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiToolBoxApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("开始发送验证码");
//        MailUtil mailUtil = new MailUtil();
//        mailUtil.
        MailUtil.send("2069966157@qq.com", "ToolBox登录验证", "您的登录验证码为：123456", false);
        System.out.println("验证码发送完毕");
    }

}
