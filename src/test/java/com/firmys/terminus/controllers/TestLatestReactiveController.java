package com.firmys.terminus.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@Profile("reactive")
public class TestLatestReactiveController {

    private static final Logger log = LoggerFactory.getLogger(TestLatestReactiveController.class);

    public TestLatestReactiveController() {
        log.info("TestLatestReactiveController created");
    }

    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("latest version");
    }

}
