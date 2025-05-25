package com.firmys.terminus.interceptors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnClass(org.springframework.web.servlet.DispatcherServlet.class)
public class TerminusMvcConfigurer implements WebMvcConfigurer {

    private final TerminusMvcInterceptor mvcInterceptor;

    public TerminusMvcConfigurer(TerminusMvcInterceptor mvcInterceptor) {
        this.mvcInterceptor = mvcInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mvcInterceptor).addPathPatterns("/api/**");
    }
}
