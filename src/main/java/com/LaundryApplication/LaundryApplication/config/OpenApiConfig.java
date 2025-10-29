package com.LaundryApplication.LaundryApplication.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI laundryAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Laundry System API")
                        .description("REST API documentation for Laundry Management System")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("LaundryApp Developer")
                                .email("support@laundryapp.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .externalDocs(new ExternalDocumentation()
                        .description("Laundry App GitHub Repository")
                        .url("https://github.com/your-repo/laundry-system"));
    }
}
