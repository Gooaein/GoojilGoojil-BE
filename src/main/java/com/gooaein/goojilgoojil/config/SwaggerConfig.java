package com.inglo.giggle.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class SwaggerConfig {
    private static final String JWT_SCHEMA_NAME = "JWT TOKEN";
    private static final String VERSION = "0.0.1";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    @Value("${server.https-address}")
    private String remoteServer;
    @Value("${server.http-address}")
    private String localServer
;
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")


                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Giggle API")
                        .description("Giggle API Docs")
                        .version(VERSION))
                .components(new Components()
                        .addSecuritySchemes(JWT_SCHEMA_NAME,
                                new SecurityScheme()
                                        .name(AUTHORIZATION_HEADER)
                                        .type(SecurityScheme.Type.HTTP)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEMA_NAME))
                .servers(List.of(
                        new io.swagger.v3.oas.models.servers.Server()
                                .url(localServer)
                                .description("Local Server"),
                        new io.swagger.v3.oas.models.servers.Server()
                                .url(remoteServer)
                                .description("Remote Server")));
    }
}
