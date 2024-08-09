package io.tmgg.kettle;


import io.github.tmgg.kettle.sdk.KettleSdk;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class KettleConfig {

    @Resource
    KettleProperties kettleProperties;

    @Bean
    public KettleSdk kettleSdk() {
        String url = kettleProperties.getUrl();
        String username = kettleProperties.getUsername();
        String password = kettleProperties.getPassword();

        String repo = kettleProperties.getRep();

        return new KettleSdk(url,repo, username, password);
    }

}
