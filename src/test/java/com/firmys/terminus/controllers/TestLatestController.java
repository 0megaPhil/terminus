package com.firmys.terminus.controllers;

import com.firmys.terminus.annotations.ApiVersion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiVersion(versions = {1, 2, 3, 4})
public class TestLatestController {

    @GetMapping("/test")
    public String test() {
        return "latest version";
    }

    @ApiVersion(versions = {3})
    @GetMapping("/3/test")
    public String test3() {
        return "version 3";
    }
}
