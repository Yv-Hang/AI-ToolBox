package com.example.aitoolbox;


import cn.hutool.core.util.RandomUtil;
import com.example.aitoolbox.controller.LoginController;
import com.example.aitoolbox.util.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiToolBoxApplicationTests {
    @Autowired
    private LoginController loginController;

    @Test
    void contextLoads() {
        try {
            Email.SendCodeMail("2069966157@qq.com", "111111");
            System.out.println("发送成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    void test() {
//        System.out.println(RandomUtil.randomNumbers(6));
        System.out.println(loginController.sendCode("2069966157@qq.com"));
    }

}
