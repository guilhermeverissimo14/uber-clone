package com.uberclone.user_service.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//DTO de resposta de autenticação (login)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;

    private String tokenType;

    private Long expiresIn;

    private UserResponseDTO user;

    //Factory method para criar response padrão.
    public static AuthResponseDTO of(String token, Long expiresIn, UserResponseDTO user) {
        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(user)
                .build();
    }
}
