package com.uberclone.ride_service.enums;

public enum CancellationReason {
    PASSENGER_CANCELLED,
    DRIVER_CANCELLED,
    NO_DRIVER_FOUND,
    TIMEOUT // Sem resposta dentro do tempo limite
}
