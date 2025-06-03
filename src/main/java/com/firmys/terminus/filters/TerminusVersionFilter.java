package com.firmys.terminus.filters;

import com.firmys.terminus.TerminusConstants;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
 * TerminusVersionFilter is a web filter component that modifies incoming HTTP requests based on
 * a custom version header. It intercepts requests, checks for the presence of a specific header,
 * and dynamically alters the request URI and request path if the header is found.
 * <p>
 * This filter helps route versioned API requests by appending the version specified in the
 * "X-Terminus-Version" header to the request URI.
 * <p>
 * An example scenario includes handling requests to differentiate API versions and route them
 * appropriately without altering the top-level server-side configurations.
 * <pre>
 * The filter operates as follows:
 * 1. Checks if the incoming request contains the "X-Terminus-Version" header.
 * 2. If present, constructs a new URI by appending the specified version and the original request path.
 * 3. Wraps and modifies the request to use the newly constructed versioned URI.
 * 4. Passes the modified request along the filter chain.
 * 5. If the header is not found, passes the original request as-is along the filter chain.
 *
 * This filter is automatically included when the application is running in a servlet-based
 * web environment. It is made conditional on the presence of WebMvcConfigurer and servlet-based
 * web applications.
 *
 * Dependencies:
 * - The component requires the use of `TerminusConstants.TERMINUS_VERSION_HEADER`, which defines
 * the header key ("X-Terminus-Version") that the filter checks for version information.
 * </pre>
 * An important design consideration for this class is ensuring that non-versioned requests are
 * unaffected and routed normally. Versioned requests are transparently modified to include the
 * version information in their URI.
 */
@Component
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
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