package com.uberclone.user_service.dto.response;

import com.uberclone.user_service.entity.Driver;
import com.uberclone.user_service.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// DTO para resposta de motorista
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverResponseDTO {

    private Long id;
    private UserResponseDTO user;
    private String licenseNumber;
    private String licenseCategory;
    private LocalDate licenseExpiresAt;
    private DriverStatus status;
    private Boolean isAvailable;
    private BigDecimal currentLatitude;
    private BigDecimal currentLongitude;
    private LocalDateTime lastLocationAt;
    private List<VehicleResponseDTO> vehicles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Converte Entity para DTO
    public static DriverResponseDTO fromEntity(Driver driver) {
        return DriverResponseDTO.builder()
                .id(driver.getId())
                .user(UserResponseDTO.fromEntity(driver.getUser()))
                .licenseNumber(driver.getLicenseNumber())
                .licenseCategory(driver.getLicenseCategory())
                .licenseExpiresAt(driver.getLicenseExpiresAt())
                .status(driver.getStatus())
                .isAvailable(driver.getIsAvailable())
                .currentLatitude(driver.getCurrentLatitude())
                .currentLongitude(driver.getCurrentLongitude())
                .lastLocationAt(driver.getLastLocationAt())
                .vehicles(driver.getVehicles().stream()
                        .map(VehicleResponseDTO::fromEntity)
                        .collect(Collectors.toList()))
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }

   //Converte Entity para DTO (sem veículos - performance)
    public static DriverResponseDTO fromEntityWithoutVehicles(Driver driver) {
        return DriverResponseDTO.builder()
                .id(driver.getId())
                .user(UserResponseDTO.fromEntity(driver.getUser()))
                .licenseNumber(driver.getLicenseNumber())
                .licenseCategory(driver.getLicenseCategory())
                .licenseExpiresAt(driver.getLicenseExpiresAt())
                .status(driver.getStatus())
                .isAvailable(driver.getIsAvailable())
                .currentLatitude(driver.getCurrentLatitude())
                .currentLongitude(driver.getCurrentLongitude())
                .lastLocationAt(driver.getLastLocationAt())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}
