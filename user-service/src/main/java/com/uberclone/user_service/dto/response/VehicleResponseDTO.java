package com.uberclone.user_service.dto.response;


import com.uberclone.user_service.entity.Vehicle;
import com.uberclone.user_service.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO para resposta de veículo
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDTO {

    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private String licensePlate;
    private VehicleType vehicleType;
    private Integer seats;
    private Boolean isActive;
    private String fullDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Converte Entity para DTO
    public static VehicleResponseDTO fromEntity(Vehicle vehicle) {
        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .color(vehicle.getColor())
                .licensePlate(vehicle.getLicensePlate())
                .vehicleType(vehicle.getVehicleType())
                .seats(vehicle.getSeats())
                .isActive(vehicle.getIsActive())
                .fullDescription(vehicle.getFullDescription())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
