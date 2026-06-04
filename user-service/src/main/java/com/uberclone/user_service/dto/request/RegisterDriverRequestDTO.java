package com.uberclone.user_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//DTO para cadastro de motorista
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDriverRequestDTO {

    @NotBlank(message = "Número da CNH é obrigatório")
    @Size(min = 11, max = 11, message = "CNH deve ter 11 dígitos")
    private String licenseNumber;

    @NotBlank(message = "Categoria da CNH é obrigatória")
    @Pattern(regexp = "^[A-E]{1,2}$", message = "Categoria inválida (ex: A, B, AB, C, D, E)")
    private String licenseCategory;

    @NotNull(message = "Data de validade da CNH é obrigatória")
    @Future(message = "CNH deve estar válida")
    private LocalDate licenseExpiresAt;
}