package com.example.demo.configuration;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info().title("API SERVICE DOCUMENT").version("v1.0.0").description("description")
						.license(new License().name("API License").url("https://google.com")))
				// cấu hình thiết lập url domain vì hiện tại đang là localhost:8080/...
				.servers(List.of(new Server().url("http://localhost:8080/").description("Server 1")))

				// version là version của API
				// description là mô tả API
				.components(new Components().addSecuritySchemes("bearerAuth",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				
				.security(List.of(new SecurityRequirement().addList("bearerAuth")));
	}

	@Bean
	public GroupedOpenApi groupedOpenApi() {
		// phải có hai cái
		// group()
		// => tên mặc định là /v3/api-doc
		// => nhưng sau này cấu hình nên để tên riêng vd api-service-1

		// packageToScan() để biết mình Scan Api ở đâu thì mình phải trỏ nó vào vd như
		// đang là ở Controller
		// mục đích là nó Scan tất cả các bean Controller nằm trong package đó để nó
		// quét.
		return GroupedOpenApi.builder().group("api-service-1").packagesToScan("com.example.demo.controller").build();
	}
}
