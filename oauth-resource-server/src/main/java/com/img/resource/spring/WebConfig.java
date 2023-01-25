package com.img.resource.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.img.resource" })
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    @Value("${base_fe_url}")
    String baseFEURL;

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(90 * 1024 * 1024);
        return multipartResolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(baseFEURL);
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
//        this.configureAsyncSupport(configurer);
        configurer.setDefaultTimeout(240000);
        log.debug("set async web timeout");
    }
}
