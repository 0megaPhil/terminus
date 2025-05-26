package com.firmys.terminus.interceptors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnClass(org.springframework.web.servlet.DispatcherServlet.class)
public class TerminusMvcConfigurer implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;

    public TerminusMvcConfigurer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new TerminusMvcInterceptor(applicationContext)).addPathPatterns("/**");
    }
}
