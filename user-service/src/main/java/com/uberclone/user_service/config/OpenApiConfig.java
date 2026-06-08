package com.uberclone.user_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Uber Clone - User Service API")
                .description("""
                    API para gerenciamento de usuários do Uber Clone.

                    ## Como autenticar
                    1. Registre um usuário em **POST /api/auth/register**
                    2. Faça login em **POST /api/auth/login** e copie o `token` da resposta
                    3. Clique no botão **Authorize** (cadeado no topo), cole o token e clique em **Authorize**
                    4. Todas as rotas autenticadas já estarão disponíveis para teste
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Uber Clone")
                    .email("guilhermeverissimo14@gmail.com")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .name("bearerAuth")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Token JWT obtido em POST /api/auth/login")));
    }
}
