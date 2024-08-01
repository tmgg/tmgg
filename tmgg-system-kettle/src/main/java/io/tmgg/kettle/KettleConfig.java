package io.tmgg.kettle;


import io.github.tmgg.kettle.sdk.KettleSdk;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KettleConfig {

    @Bean
    public KettleSdk kettleSdk() {
        String url = "http://127.0.0.1:8080";
        String username = "cluster";
        String password = "cluster";

        String repo = "test-repo";

        return new KettleSdk(url,repo, username, password);
    }

}
