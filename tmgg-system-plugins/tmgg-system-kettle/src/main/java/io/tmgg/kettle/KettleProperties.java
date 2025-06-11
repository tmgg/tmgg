package io.tmgg.kettle;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "tmgg.kettle")
@Data
public class KettleProperties {

    String url = "http://127.0.0.1:8080";
    String username = "cluster";
    String password = "cluster";
    String rep = "kettle-repository";

}
