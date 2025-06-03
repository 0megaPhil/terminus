package com.firmys.terminus;

import com.firmys.terminus.annotations.Terminus;
import com.firmys.terminus.annotations.TerminusMapping;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.Map;

/**
 * TerminusHandlerMapping is a specialized implementation of {@link RequestMappingHandlerMapping}
 * that processes mappings annotated with the custom {@link Terminus} annotation. This class
 * dynamically adjusts request mappings based on versioning metadata provided by the
 * {@link Terminus} annotations in the controller classes.
 * <p>
 * This component extends the default request mapping handler functionality in Spring MVC
 * by introducing support for versioned endpoint registrations. It ensures that each version
 * defined in a {@link Terminus} annotation is mapped as a separate {@link RequestMappingInfo}.
 * <p>
 * Features:
 * <li>Automatically unregisters default request mappings for methods annotated with {@link Terminus}.</li>
 * <li>Registers new mappings by appending version information to the endpoint paths.</li>
 * <li>Processes mapping metadata such as HTTP methods, path patterns, headers, and media types.</li>
 * <p>
 * Lifecycle:
 * The {@code afterPropertiesSet} method is overridden to process {@link Terminus} annotations
 * and adjust the handler mappings during initialization.
 * <p>
 * Custom Processing:
 * <li>{@link #processTerminusAnnotations}: Iterates over all request mappings to identify
 * {@link Terminus}-annotated methods, unregister default mappings, and register
 * versioned mappings.</li>
 * <li>{@link #createMappingForTerminus}: Constructs a {@link RequestMappingInfo} instance
 * for each version specified in a {@link Terminus} annotation.</li>
 */
@Component
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnMissingBean(TerminusMvcHandlerMapping.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class TerminusMvcHandlerMapping extends RequestMappingHandlerMapping implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        processTerminusAnnotations();
    }

    private void processTerminusAnnotations() {
        getHandlerMethods().entrySet()
                .forEach(this::processTerminusAnnotation);
    }

    private void processTerminusAnnotation(Map.Entry<RequestMappingInfo, HandlerMethod> entry) {
        Arrays.stream(entry.getValue().getBeanType().getAnnotationsByType(Terminus.class))
                .filter(term -> term.versions().length > 0)
                .peek(term -> unregisterMapping(entry.getKey()))
                .forEach(term -> Arrays.stream(term.versions())
                        .distinct()
                        .forEach(ver -> registerMapping(
                                createMappingForTerminus(entry, ver),
                                entry.getValue().getBean(),
                                entry.getValue().getMethod())));
    }

    /*
     * Creates a versioned `RequestMappingInfo` instance based on the provided
     * entry and version. This method adjusts the endpoint paths to include the specified
     * version as a prefix while preserving mappings defined at the method level using
     * `TerminusMapping`.
     */
    private RequestMappingInfo createMappingForTerminus(
            Map.Entry<RequestMappingInfo, HandlerMethod> entry,
            String version) {

        // Get the paths from TerminusMapping at method level
        String[] methodPaths = Arrays.stream(
                        entry.getValue().getMethod().getAnnotationsByType(TerminusMapping.class))
                .findFirst()
                .map(mapping ->
                        mapping.value().length > 0
                                ? mapping.value()
                                : mapping.path())
                .orElse(new String[0]);

        // Create versioned paths
        String[] versionedPaths;
        if (methodPaths.length == 0) {
            versionedPaths = new String[]{"/" + version};
        } else {
            versionedPaths = Arrays.stream(methodPaths)
                    .map(path -> "/" + version + (path.startsWith("/") ? path : "/" + path))
                    .toArray(String[]::new);
        }

        return RequestMappingInfo
                .paths(versionedPaths)
                .build();
    }
}