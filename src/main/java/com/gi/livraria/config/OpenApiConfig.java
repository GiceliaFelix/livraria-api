package com.gi.livraria.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI livrariaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Livraria API")
                .description("API REST para catalogo de livros, com integracao a Google Books API")
                .version("1.0.0"));
    }
}
