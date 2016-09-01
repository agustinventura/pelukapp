package com.spanishcoders.configuration;

import org.springframework.boot.autoconfigure.web.WebMvcRegistrationsAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@Profile(value = {"development", "integration", "production"})
public class WebMvcConfiguration {

    @Bean
    public WebMvcRegistrationsAdapter enableMatrixVariables() {
        return new WebMvcRegistrationsAdapter() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();
                //Semicolons are needed to use matrix variables in URLs such as /hairdresser/blocks/free/works=1;works=2
                requestMappingHandlerMapping.setRemoveSemicolonContent(false);
                return requestMappingHandlerMapping;
            }
        };
    }

    @Bean
    public WebMvcConfigurerAdapter additionalResourceHandlers() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                super.addResourceHandlers(registry);
                registry.addResourceHandler("swagger-ui.html")
                        .addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");
            }
        };
    }
}
