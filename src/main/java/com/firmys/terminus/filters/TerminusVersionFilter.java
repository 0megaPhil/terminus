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