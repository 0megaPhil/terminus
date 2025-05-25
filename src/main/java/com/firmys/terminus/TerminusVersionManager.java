package com.firmys.terminus;

import com.firmys.terminus.annotations.ApiVersion;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.firmys.terminus.TerminusConfig.CONTROLLER_API_VERSIONS_BEAN_NAME;

@Component
class TerminusVersionManager {
    private final Map<Class<?>, ApiVersion> controllerApiVersions;
    private final Map<Class<?>, Map<Method, ApiVersion>> controllerEndpointApiVersions;

    TerminusVersionManager(
            @Qualifier(CONTROLLER_API_VERSIONS_BEAN_NAME) Map<Class<?>, ApiVersion> controllerApiVersions) {
        this.controllerApiVersions = controllerApiVersions;
        this.controllerEndpointApiVersions = controllerApiVersions.entrySet().stream().parallel()
                .map(entry -> Map.entry(entry.getKey(), Arrays.stream(entry.getKey().getDeclaredMethods())
                        .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                                .anyMatch(anno -> RequestMapping.class.isAssignableFrom(anno.annotationType())))
                        .map(method -> Optional.of(method)
                                .filter(m -> m.isAnnotationPresent(ApiVersion.class))
                                .map(m -> m.getAnnotation(ApiVersion.class))
                                .map(an -> Map.entry(method, an))
                                .orElse(Map.entry(method, entry.getValue())))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
