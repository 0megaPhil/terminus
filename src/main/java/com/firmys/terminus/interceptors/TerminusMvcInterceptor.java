package com.firmys.terminus.interceptors;

import com.firmys.terminus.TerminusConstants;
import com.firmys.terminus.TerminusVersionManager;

import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TerminusMvcInterceptor implements HandlerInterceptor {

    private final TerminusVersionManager terminusVersionManager;

    public TerminusMvcInterceptor(ApplicationContext applicationContext) {
        terminusVersionManager = new TerminusVersionManager(applicationContext);
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {

        Integer endpointVersion = Optional.ofNullable(request.getHeader(TerminusConstants.TERMINUS_VERSION_HEADER))
                .map(Integer::parseInt)
                .orElseGet(terminusVersionManager::latestApiVersion);

        if (handler instanceof HandlerMethod methodHandler) {
            Map.Entry<Method, Object> methodObjectEntry = terminusVersionManager
                    .getVersionedMethodBeanEntry(endpointVersion, methodHandler.getMethod());
            InvocableHandlerMethod resolvedHandler = new InvocableHandlerMethod(
                    methodObjectEntry.getValue(), methodObjectEntry.getKey());

            // The RequestMappingHandlerAdapter will configure the resolvedHandler
            // with necessary argument resolvers and parameter name discoverer.
            // Thus, manual copying is not needed here.

            // Replace the handler in the current request attribute
            request.setAttribute(
                    // Qualify HandlerMapping
                    HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE,
                    resolvedHandler);
        }
        return true;
    }
}
