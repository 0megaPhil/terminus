package com.firmys.terminus.controllers;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("!reactive")
public class TestLatestController {

    @GetMapping("/test")
    public String test() {
        return "latest version";
    }
}
