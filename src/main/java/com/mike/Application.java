package com.mike;

import com.mike.config.PubSubConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties(PubSubConfiguration.class)
public class Application{

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }
}
