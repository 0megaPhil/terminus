package com.firmys.terminus;

import com.firmys.terminus.annotations.ApiVersion;

import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Nullable;

public class TerminusVersionManager {
    private final ApplicationContext applicationContext;
    private final Map<Object, ApiVersion> controllerApiVersions;
    private final Map<Integer, Map<Method, Object>> versionedMethodBeanMap;
    private final Map<Object, Map<Method, ApiVersion>> controllerEndpointApiVersions;

    public TerminusVersionManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.controllerApiVersions = controllerApiVersions();

        this.controllerEndpointApiVersions = controllerApiVersions.entrySet().stream().parallel()
                .map(entry -> Map.entry(entry.getKey(), Arrays.stream(entry.getKey().getClass().getDeclaredMethods())
                        .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                                .anyMatch(anno -> RequestMapping.class.isAssignableFrom(anno.annotationType())))
                        .map(method -> Optional.of(method)
                                .map(m -> m.getAnnotation(ApiVersion.class))
                                .map(an -> Map.entry(method, an))
                                .orElse(Map.entry(method, entry.getValue())))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        this.versionedMethodBeanMap = controllerEndpointApiVersions.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .flatMap(methodEntry -> Arrays.stream(methodEntry.getValue().allowed()).boxed()
                                .map(
                                        ver -> Map.entry(ver,
                                                Map.entry(methodEntry.getKey(), entry.getKey())))))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.toMap(
                                e -> e.getValue().getKey(),
                                e -> e.getValue().getValue(),
                                (existing, replacement) -> replacement)));
    }

    public Map.Entry<Method, Object> getVersionedMethodBeanEntry(int version, Method method, @Nullable Object... args) {
        Map.Entry<Method, Object> methodController = versionedMethodBeanMap.get(version).entrySet().stream()
                .filter(entry -> Objects.equals(entry.getKey().getName(), method.getName()))
                .filter(entry -> entry.getKey().getParameterCount() == method.getParameterCount())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No method found for version " + version));
        return methodController;
    }

    @Nullable
    private <O> O callMethodController(Map.Entry<Method, Object> methodController, @Nullable Object... args) {
        Method method = methodController.getKey();
        Object bean = methodController.getValue();
        try {
            if (method.getReturnType().equals(Void.TYPE)) {
                method.invoke(bean, args);
                return null;
            }
            return (O) method.invoke(bean, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int latestApiVersion() {
        return controllerEndpointApiVersions.values().stream()
                .flatMap(set -> set.values().stream())
                .flatMap(anno -> Arrays.stream(anno.allowed()).boxed())
                .max(Comparator.comparingInt(i -> i))
                .orElse(0);
    }

    public Set<Integer> allowedVersions(Class<?> controllerClass, Method method) {
        return Optional.ofNullable(controllerEndpointApiVersions.get(controllerClass))
                .map(methodMap -> methodMap.get(method))
                .map(av -> Arrays.stream(av.allowed()).boxed())
                .orElse(Stream.<Integer>builder().build())
                .collect(Collectors.toSet());
    }

    private Map<Object, ApiVersion> controllerApiVersions() {
        return Arrays.stream(applicationContext.getBeanDefinitionNames())
                .map(applicationContext::getBean)
                .filter(bean -> bean.getClass().isAnnotationPresent(ApiVersion.class))
                .filter(bean -> bean.getClass().isAnnotationPresent(RestController.class))
                .map(bean -> Map.entry(bean, bean.getClass().getAnnotation(ApiVersion.class)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
