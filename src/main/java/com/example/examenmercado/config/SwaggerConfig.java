package com.example.examenmercado.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI().info(new Info()
                .title("API para detectar Mutantes")
                .version("1.0")
                .description("API Rest que detecta si un humano es mutante o no dependiendo de su secuencia de ADN.")
        );
    }

}