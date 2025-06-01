package com.firmys.terminus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TerminusMvcConfig implements WebMvcConfigurer {
    @Bean
    TerminusHandlerMapping terminusHandlerMapping() {
        TerminusHandlerMapping handlerMapping = new TerminusHandlerMapping();
        handlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return handlerMapping;
    }
}
