package com.uberclone.ride_service.dto.response;

import com.uberclone.ride_service.dto.RideLocationDTO;
import com.uberclone.ride_service.entity.Ride;
import com.uberclone.ride_service.enums.CancellationReason;
import com.uberclone.ride_service.enums.PaymentMethod;
import com.uberclone.ride_service.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideResponseDTO {

    private Long id;
    private Long passengerId;
    private Long driverId;
    private RideStatus status;
    private RideLocationDTO origin;
    private RideLocationDTO destination;
    private BigDecimal estimatedPrice;
    private BigDecimal finalPrice;
    private PaymentMethod paymentMethod;
    private Integer passengerRating;
    private Integer driverRating;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private CancellationReason cancellationReason;
    private LocalDateTime createdAt;

    public static RideResponseDTO fromEntity(Ride ride) {
        return RideResponseDTO.builder()
                .id(ride.getId())
                .passengerId(ride.getPassengerId())
                .driverId(ride.getDriverId())
                .status(ride.getStatus())
                .origin(RideLocationDTO.builder()
                        .latitude(ride.getOrigin().getLatitude())
                        .longitude(ride.getOrigin().getLongitude())
                        .address(ride.getOrigin().getAddress())
                        .build())
                .destination(RideLocationDTO.builder()
                        .latitude(ride.getDestination().getLatitude())
                        .longitude(ride.getDestination().getLongitude())
                        .address(ride.getDestination().getAddress())
                        .build())
                .estimatedPrice(ride.getEstimatedPrice())
                .finalPrice(ride.getFinalPrice())
                .paymentMethod(ride.getPaymentMethod())
                .passengerRating(ride.getPassengerRating())
                .driverRating(ride.getDriverRating())
                .acceptedAt(ride.getAcceptedAt())
                .startedAt(ride.getStartedAt())
                .completedAt(ride.getCompletedAt())
                .cancelledAt(ride.getCancelledAt())
                .cancellationReason(ride.getCancellationReason())
                .createdAt(ride.getCreatedAt())
                .build();
    }
}

