package com.firmys.terminus.filters;

import com.firmys.terminus.TerminusConstants;
import com.firmys.terminus.TerminusVersionManager;

import org.springframework.context.ApplicationContext;
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
public class RedirectFilter implements Filter {

    private final ApplicationContext applicationContext;
    private final TerminusVersionManager versionManager;

    public RedirectFilter(
            ApplicationContext applicationContext, TerminusVersionManager versionManager) {
        this.applicationContext = applicationContext;
        this.versionManager = versionManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        versionManager.initialize(applicationContext);

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
        Integer endpointVersion = Optional.ofNullable(
                        request.getHeader(TerminusConstants.TERMINUS_VERSION_HEADER))
                .map(Integer::parseInt)
                .orElseGet(versionManager::latestApiVersion);
        return endpointVersion != versionManager.latestApiVersion(); // Example
    }

    private String buildVersionedUri(HttpServletRequest request) {
        Integer endpointVersion = Optional.ofNullable(
                        request.getHeader(TerminusConstants.TERMINUS_VERSION_HEADER))
                .map(Integer::parseInt)
                .orElseGet(versionManager::latestApiVersion);
        return "/" + endpointVersion + request.getRequestURI(); // Example
    }
}