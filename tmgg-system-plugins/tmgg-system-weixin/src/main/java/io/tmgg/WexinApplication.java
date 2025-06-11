package io.tmgg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WexinApplication {

    public static void main(String[] args) {
        SpringApplication.run(WexinApplication.class, args);
        System.out.println("启动成功..........");
    }

}
