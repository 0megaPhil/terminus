package com.firmys.terminus;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
class TerminusConfig {

    //    @Bean
//    @Lazy
    TerminusVersionManager terminusVersionManager(ApplicationContext applicationContext) {
        return new TerminusVersionManager(applicationContext);
    }
}
