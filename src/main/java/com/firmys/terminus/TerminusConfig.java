package com.firmys.terminus;

import com.firmys.terminus.annotations.ApiVersion;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


@Configuration
class TerminusConfig {
    static final String CONTROLLER_API_VERSIONS_BEAN_NAME = "bean-terminus-controllerApiVersions";

    @Bean(CONTROLLER_API_VERSIONS_BEAN_NAME)
    Map<Class<?>, ApiVersion> controllerApiVersions(ConfigurableApplicationContext applicationContext) {
        return Arrays.stream(applicationContext.getBeanDefinitionNames())
                .map(applicationContext::getBean)
                .filter(bean -> bean.getClass().isAnnotationPresent(ApiVersion.class))
                .filter(bean -> bean.getClass().isAnnotationPresent(RestController.class))
                .map(bean -> Map.entry(bean.getClass(), bean.getClass().getAnnotation(ApiVersion.class)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}