package com.chamindu.taskManager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Tells Spring : This class contains configuration information for the OpenAPI documentation of the application.
@Configuration
public class OpenApiConfig {
    //Creates an OpenAPI configuration object when the application starts.
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                //Adds information displayed at the top of Swagger UI
                .info(new Info()
                        .title("Task Manager API")
                        .description(
                            "REST API for managing user-specific study tasks, " +
                            "authentication and AI study suggestions."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                            .name("Chamindu Kaushalya")
                        )
                )
                .components(new Components()
                            //This API uses JWT Bearer authentication.
                            .addSecuritySchemes(
                                "bearerAuth", 
                                new SecurityScheme()
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT")
                           )
                );
                
    }
    
}
