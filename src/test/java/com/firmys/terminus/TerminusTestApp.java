package com.firmys.terminus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Profile("!reactive")
@SpringBootApplication(scanBasePackages = "com.firmys.terminus")
public class TerminusTestApp {

    public static void main(String[] args) {
        SpringApplication.run(TerminusTestApp.class, args);
    }
}
