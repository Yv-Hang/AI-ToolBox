package com.example.aitoolbox.util;

import com.example.aitoolbox.controller.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailTest {
    @Autowired
    LoginController controller;

    @Test
    void Mail(){
        controller.sendCode("hyc20040730@163.com");
    }
}
