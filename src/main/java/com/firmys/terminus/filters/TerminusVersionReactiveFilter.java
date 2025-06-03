package com.firmys.terminus.filters;

import com.firmys.terminus.TerminusConstants;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * TerminusVersionReactiveFilter is a reactive web filter that modifies the request path based
 * on the presence of a specific versioning header, "X-Terminus-Version". This filter sits
 * in the WebFlux processing chain and dynamically updates the request URI to include the version
 * for routing purposes.
 * <p>
 * This filter is specifically designed for reactive web applications using Spring WebFlux
 * and is conditionally activated for applications running in a reactive environment.
 * <pre>
 * Core functionality includes:
 * - Intercepting incoming requests in a reactive environment.
 * - Detecting the presence of the "X-Terminus-Version" header in the request.
 * - Dynamically constructing and applying a versioned URI based on the header value.
 * - Passing the modified (or unmodified) request along the reactive filter chain.
 *
 * Primary methods:
 * - The `filter` method is the entry point for request processing, where the detection and modification of
 *   request URIs occur.
 * - Private helper methods `shouldUseVersionedUri` and `buildVersionedUri` assist in determining whether
 *   the request needs to be versioned and constructing the altered URI respectively.
 *
 * Key considerations:
 * - Requests without a versioning header are passed along the filter chain unmodified.
 * - The filter leverages Spring's `WebFilter` interface for reactive environments.
 * - The header key for detecting version information is defined in `TerminusConstants.TERMINUS_VERSION_HEADER`.
 * </pre>
 * This filter plays a crucial role in enabling versioned routing for APIs in a Spring WebFlux-powered
 * reactive environment while maintaining compatibility with existing non-versioned requests.
 */
@Component
@ConditionalOnClass(WebFluxConfigurer.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class TerminusVersionReactiveFilter implements WebFilter {

    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (shouldUseVersionedUri(request)) {
            String newPath = buildVersionedUri(request);

            // Create new request with a modified path
            ServerHttpRequest newRequest = request.mutate()
                    .path(newPath)
                    .build();

            // Create new exchange with modified request
            ServerWebExchange newExchange = exchange.mutate()
                    .request(newRequest)
                    .build();

            return chain.filter(newExchange);
        }

        return chain.filter(exchange);
    }

    private boolean shouldUseVersionedUri(ServerHttpRequest request) {
        return request.getHeaders().containsKey(TerminusConstants.TERMINUS_VERSION_HEADER);
    }

    private String buildVersionedUri(ServerHttpRequest request) {
        String endpointVersion = request.getHeaders()
                .getFirst(TerminusConstants.TERMINUS_VERSION_HEADER);
        return "/" + (endpointVersion != null ? endpointVersion : "") + request.getPath().value();
    }

}
