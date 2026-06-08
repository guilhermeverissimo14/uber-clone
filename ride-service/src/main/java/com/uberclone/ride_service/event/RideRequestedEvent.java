package com.uberclone.ride_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestedEvent {

    private Long rideId;
    private Long passengerId;

    // Origem
    private BigDecimal originLatitude;
    private BigDecimal originLongitude;
    private String originAddress;

    // Destino
    private BigDecimal destinationLatitude;
    private BigDecimal destinationLongitude;
    private String destinationAddress;
}
