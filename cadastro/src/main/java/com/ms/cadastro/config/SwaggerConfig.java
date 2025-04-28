package com.ms.cadastro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title("API de Cadastro de PETS").version("1.0")
				.description("API para gerenciar pets, suas raças e seus donos"));
	}
}
