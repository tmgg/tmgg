
package io.tmgg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class SysBootApplication {


    public static void main(String[] args) {
        SpringApplication.run(SysBootApplication.class, args);
        System.out.println("启动完成");
    }

}
