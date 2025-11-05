package org.example.eventm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eventManagementAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("/").description("Default Server URL")
                ))
                .info(new Info()
                        .title("Event Management API")
                        .version("1.0")
                        .description("API documentation for the Event Management System"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }
}
