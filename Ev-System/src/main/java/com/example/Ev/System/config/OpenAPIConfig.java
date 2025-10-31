package com.example.Ev.System.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("EV Service Center API")
                .version("v1")
                .description("APIs cho hệ thống EV Service Center")
                .contact(new Contact().name("Team EV Service Center")));
    }
}
