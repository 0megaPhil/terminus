package com.firmys.terminus.controllers;

import com.firmys.terminus.annotations.ApiVersion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiVersion(allowed = {1})
public class TestLatestController {

    @GetMapping("/test")
    public String test() {
        return "current version";
    }
}
