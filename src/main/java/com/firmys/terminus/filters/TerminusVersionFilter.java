package com.firmys.terminus.filters;

import com.firmys.terminus.TerminusConstants;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * TerminusVersionFilter is a Spring-managed component that implements the {@link Filter} interface.
 * This filter is responsible for dynamically modifying request URIs by prefixing them with a version
 * identifier retrieved from a specific HTTP header. It allows version-specific routing of incoming
 * requests in a web application.
 * <p>
 * Functionality:
 * <li> Intercepts incoming HTTP requests.
 * <li> Checks if the request contains a version identifier in the header specified by
 * {@code TerminusConstants.TERMINUS_VERSION_HEADER}.
 * <li> If a version identifier is present, it rewrites the request URI to include the version as a
 * prefix. For example, a request to `/example` with version `1` becomes `/1/example`.
 * <li> Passes the updated or original request down the filter chain for further processing.
 * <p>
 * Key Methods:
 * <li> {@link #doFilter(ServletRequest, ServletResponse, FilterChain)}: Core method that performs
 * the filtering and URI modification logic.
 * <li> {@code shouldUseVersionedUri(HttpServletRequest)}: Determines if the request URI needs to be
 * prefixed with a version based on the presence of a version header.
 * <li> {@code buildVersionedUri(HttpServletRequest)}: Constructs the versioned URI by combining
 * the version identifier and the original request URI.
 * <p>
 * Integration:
 * This filter works seamlessly with components like `TerminusHandlerMapping` to ensure
 * dynamic request routing to specific endpoints based on version information. It is configured as
 * a Spring component for automatic inclusion in the filter chain via dependency injection.
 * <p>
 * Advantages:
 * <li> Provides a simple mechanism for handling versioned APIs.
 * <li> Decouples URI manipulation logic from controller-level concerns.
 * <li> Ensures request routing is consistent and extensible for multi-versioned applications.
 * <p>
 * Usage Context:
 * Typically used in applications that support API versioning, especially where version information
 * is specified in headers rather than directly in the request path.
 * <p>
 * Related Components:
 * <li> {@code TerminusConstants}: Defines the constants used for identifying the HTTP header that
 * specifies the version.
 * <li> {@code TerminusHandlerMapping}: Handles dynamic request mapping for versioned endpoints.
 */
@Component
public class TerminusVersionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (shouldUseVersionedUri(httpRequest)) {
            String newPath = buildVersionedUri(httpRequest);

            // Create a wrapped request with the new URI
            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getRequestURI() {
                    return newPath;
                }

                @Override
                public String getServletPath() {
                    return newPath;
                }

                @Override
                public StringBuffer getRequestURL() {
                    StringBuffer url = new StringBuffer();
                    String scheme = getScheme();
                    int port = getServerPort();

                    url.append(scheme).append("://").append(getServerName());

                    if (port > 0 && ((scheme.equals("http") && port != 80)
                            || (scheme.equals("https") && port != 443))) {
                        url.append(':').append(port);
                    }

                    url.append(newPath);
                    return url;
                }
            };
            chain.doFilter(wrappedRequest, response);
            return;
        }

        // Continue with the filter chain if no redirect is needed
        chain.doFilter(request, response);
    }

    private boolean shouldUseVersionedUri(HttpServletRequest request) {
        return request.getHeader(TerminusConstants.TERMINUS_VERSION_HEADER) != null;
    }

    private String buildVersionedUri(HttpServletRequest request) {
        String endpointVersion = Optional.ofNullable(
                        request.getHeader(TerminusConstants.TERMINUS_VERSION_HEADER))
                .orElse("");
        return "/" + endpointVersion + request.getRequestURI();
    }
}