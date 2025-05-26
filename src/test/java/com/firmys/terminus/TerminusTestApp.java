package com.firmys.terminus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.firmys.terminus")
public class TerminusTestApp {

    public static void main(String[] args) {
        SpringApplication.run(TerminusTestApp.class, args);
    }
}
