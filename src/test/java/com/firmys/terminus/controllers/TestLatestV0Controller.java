package com.firmys.terminus.controllers;

import com.firmys.terminus.annotations.ApiVersion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiVersion(allowed = {0})
public class TestLatestV0Controller {

    @GetMapping("/0/test")
    public String test() {
        return "old version";
    }
}
