package io.tmgg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestBootApplication.class, args);
        System.out.println("启动完成");
    }
}
