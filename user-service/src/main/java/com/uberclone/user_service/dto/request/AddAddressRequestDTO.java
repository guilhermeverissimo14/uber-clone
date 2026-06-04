package com.uberclone.user_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

//DTO Para adicionar endereço
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddAddressRequestDTO {

    @NotBlank(message = "Rua é obrigatória")
    private String street;

    private String number;

    private String complement;

    private String neighborhood;

    @NotBlank(message = "Cidade é obrigatória")
    private String city;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (ex: SP)")
    private String state;

    @Pattern(regexp = "^\\d{8}$", message = "CEP deve ter 8 dígitos")
    private String zipCode;

    @NotNull(message = "Latitude é obrigatória")
    @DecimalMin(value = "-90.0", message = "Latitude inválida")
    @DecimalMax(value = "90.0", message = "Latitude inválida")
    private BigDecimal latitude;

    @NotNull(message = "Longitude é obrigatória")
    @DecimalMin(value = "-180.0", message = "Longitude inválida")
    @DecimalMax(value = "180.0", message = "Longitude inválida")
    private BigDecimal longitude;

    @Pattern(regexp = "^(HOME|WORK|OTHER)$", message = "Label deve ser HOME, WORK ou OTHER")
    private String label;

    private Boolean isDefault;
}
