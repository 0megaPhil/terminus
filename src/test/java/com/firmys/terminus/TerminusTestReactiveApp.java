package com.firmys.terminus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@Profile("reactive")
@SpringBootApplication(scanBasePackages = "com.firmys.terminus")
public class TerminusTestReactiveApp {

    public static void main(String[] args) {
        SpringApplication.run(TerminusTestReactiveApp.class, args);
    }
}
