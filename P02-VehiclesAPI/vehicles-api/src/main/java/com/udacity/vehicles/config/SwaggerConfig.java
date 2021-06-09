package com.udacity.vehicles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Configuration
@EnableSwagger2
public class SwaggerConfig{

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(regex("/cars.*")::test)
                .build()
                //.pathMapping("/")
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false);
    }

    private Predicate<String> regex(String regex) {
        Predicate<String> predicate = Pattern
                                .compile(regex)
                                .asPredicate();
        return predicate;
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Vehicle Service API",
                "This API returns a list of dog and it's properties.",
                "1.0",
                "http://www.udacity.com/tos",
                new Contact("Emmanuel Nwabudo", "www.neookpara.io", "me@neookpara.io"),
                "License of API", "http://www.udacity.com/license", Collections.emptyList());
    }



}
