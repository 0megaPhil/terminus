package com.firmys.terminus.controllers;

import com.firmys.terminus.annotations.Terminus;
import com.firmys.terminus.annotations.TerminusMapping;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMethod;

@Profile("!reactive")
@Terminus(versions = {"0", "1", "2"})
public class TestLatestV0Controller {

    @TerminusMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        return "versions 0, 1, 2";
    }
}
