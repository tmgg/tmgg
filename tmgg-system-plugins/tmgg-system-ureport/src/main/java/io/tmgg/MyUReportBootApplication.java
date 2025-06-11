
package io.tmgg;

import com.bstek.ureport.UReportProperties;
import io.tmgg.report.provider.UReport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


@SpringBootApplication
public class MyUReportBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyUReportBootApplication.class, args);
    }

}
