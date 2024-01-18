package com.dyonyon.The10000HourRule.configuration;

import com.dyonyon.The10000HourRule.Common.APIInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@org.springframework.context.annotation.Configuration
public class Configuration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiInterceptor());
    }

    @Bean
    public APIInterceptor apiInterceptor() {
        return new APIInterceptor();
    }

}
