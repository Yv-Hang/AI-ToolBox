package com.example.aitoolbox.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 *  项目启动成功后打印项目 logo
 */
@Component
@Slf4j
public class BannerIsHangApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
//        log.info(BANNER);
    }
    /**
     * 项目logo 常量
     */
    public static final String BANNER =
            """
                 The project is starting...
                                                            \s
                     ,--.  ,--.                             \s
                     |  '--'  |  ,--,--. ,---_---. ,-. .-.  \s
                     |  .--.  | ' ,-.  | |  .-.  | | | | |  \s
                     |  |  |  | \\ '-'  | |  | |  | \\ '-' /\s
                     `--'  `--'  `--`--' `--' `--' .`-  /   \s
                                                   `---'    \s
                   --The project was started successfully.--""";
}