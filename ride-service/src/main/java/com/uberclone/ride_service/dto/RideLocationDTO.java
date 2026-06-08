package com.uberclone.ride_service.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideLocationDTO {

    @NotNull(message = "Latitude é obrigatória")
    @DecimalMin(value = "-90.0",  message = "Latitude mínima é -90")
    @DecimalMax(value = "90.0",   message = "Latitude máxima é 90")
    private BigDecimal latitude;

    @NotNull(message = "Longitude é obrigatória")
    @DecimalMin(value = "-180.0", message = "Longitude mínima é -180")
    @DecimalMax(value = "180.0",  message = "Longitude máxima é 180")
    private BigDecimal longitude;

    @NotBlank(message = "Endereço é obrigatório")
    private String address;
}
