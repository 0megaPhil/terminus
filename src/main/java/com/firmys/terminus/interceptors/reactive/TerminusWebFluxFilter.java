package com.firmys.terminus.interceptors.reactive;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnClass(org.springframework.web.reactive.DispatcherHandler.class)
public class TerminusWebFluxFilter implements WebFilter {
    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getRequest().getURI();
        return chain.filter(exchange);
    }
}
