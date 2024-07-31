
package io.tmgg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;


@SpringBootApplication
@EnableCaching
@EnableAsync
public class BootApplication {


    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }

}
