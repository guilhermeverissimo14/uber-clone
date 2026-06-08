package com.uberclone.ride_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Uber Clone - Ride Service API")
                .description("""
                    API para gerenciamento de corridas do Uber Clone.
                    
                    ## Fluxo de uma corrida
                    1. Passageiro solicita corrida → **POST /api/rides**
                    2. Motorista aceita → **PATCH /api/rides/{id}/status** com status `ACCEPTED`
                    3. Motorista inicia → **PATCH /api/rides/{id}/status** com status `IN_PROGRESS`
                    4. Motorista finaliza → **PATCH /api/rides/{id}/status** com status `COMPLETED`
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Uber Clone")
                    .email("guilhermeverissimo14@gmail.com")));
    }
}
