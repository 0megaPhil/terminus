package com.firmys.terminus.controllers;

import com.firmys.terminus.annotations.Terminus;
import com.firmys.terminus.annotations.TerminusMapping;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.config.EnableWebFlux;

import reactor.core.publisher.Mono;

@Profile("reactive")
@EnableWebFlux
@Terminus(versions = {"0", "1", "2"})
public class TestLatestV0ReactiveController {

    @TerminusMapping(value = "/test", method = RequestMethod.GET)
    public Mono<String> test() {
        return Mono.just("versions 0, 1, 2");
    }
}
