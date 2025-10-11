package com.flowershop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Для загружаемых файлов из папки uploads - УБЕРИТЕ /flowershop/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // Для статических ресурсов - УБЕРИТЕ /flowershop/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirect to home page
        registry.addViewController("/").setViewName("redirect:/home");
        registry.addViewController("/home").setViewName("index");

        // Auth pages
        registry.addViewController("/login").setViewName("auth/login");
        registry.addViewController("/register").setViewName("auth/register");

        // Error pages
        registry.addViewController("/access-denied").setViewName("error/access-denied");
    }
}