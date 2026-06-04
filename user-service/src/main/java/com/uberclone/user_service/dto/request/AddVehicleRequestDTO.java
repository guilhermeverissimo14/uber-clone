package com.uberclone.user_service.dto.request;

import com.uberclone.user_service.enums.VehicleType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//DTO Para adicionar veículo.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddVehicleRequestDTO {

    @NotBlank(message = "Marca é obrigatória")
    private String brand;

    @NotBlank(message = "Modelo é obrigatório")
    private String model;

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 2000, message = "Ano deve ser maior ou igual a 2000")
    @Max(value = 2030, message = "Ano inválido")
    private Integer year;

    @NotBlank(message = "Cor é obrigatória")
    private String color;

    @NotBlank(message = "Placa é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}\\d{4}$|^[A-Z]{3}\\d[A-Z]\\d{2}$",
            message = "Placa inválida (formato antigo: ABC1234 ou Mercosul: ABC1D23)")
    private String licensePlate;

    @NotNull(message = "Tipo de veículo é obrigatório")
    private VehicleType vehicleType;

    @Min(value = 2, message = "Veículo deve ter no mínimo 2 assentos")
    @Max(value = 8, message = "Veículo deve ter no máximo 8 assentos")
    private Integer seats;
}
