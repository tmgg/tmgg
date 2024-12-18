package io.tmgg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WeappApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeappApplication.class, args);
        System.out.println("启动成功..........");
    }

}
