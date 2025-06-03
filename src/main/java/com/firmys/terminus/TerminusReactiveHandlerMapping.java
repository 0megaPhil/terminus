package com.firmys.terminus;

import com.firmys.terminus.annotations.Terminus;
import com.firmys.terminus.annotations.TerminusMapping;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.Map;

@Component
@ConditionalOnClass(WebFluxConfigurer.class)
@ConditionalOnMissingBean(TerminusReactiveHandlerMapping.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class TerminusReactiveHandlerMapping extends RequestMappingHandlerMapping implements InitializingBean {

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

    private RequestMappingInfo createMappingForTerminus(
            Map.Entry<RequestMappingInfo, HandlerMethod> entry,
            String version) {

        String[] methodPaths = Arrays.stream(
                        entry.getValue().getMethod().getAnnotationsByType(TerminusMapping.class))
                .findFirst()
                .map(mapping ->
                        mapping.value().length > 0
                                ? mapping.value()
                                : mapping.path())
                .orElse(new String[0]);

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