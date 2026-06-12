package com.uberclone.location_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLocationResponseDTO {

    private Long driverId;
    private Double latitude;
    private Double longitude;
    private Double distanceKm; // preenchido apenas na busca por proximidade
}

