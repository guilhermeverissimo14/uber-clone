package com.uberclone.ride_service.entity;

import com.uberclone.ride_service.enums.CancellationReason;
import com.uberclone.ride_service.enums.PaymentMethod;
import com.uberclone.ride_service.enums.RideStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // IDs dos usuários do user-service (sem @ManyToOne, pois são microsserviços
    // separados)
    @Column(name = "passenger_id", nullable = false)
    private Long passengerId;

    @Column(name = "driver_id")
    private Long driverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RideStatus status = RideStatus.REQUESTED;

    // Localização de origem embutida na tabela rides
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "pickup_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "pickup_longitude")),
            @AttributeOverride(name = "address", column = @Column(name = "pickup_address"))
    })
    private RideLocation origin;

    // Localização de destino embutida na tabela rides
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "dropoff_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "dropoff_longitude")),
            @AttributeOverride(name = "address", column = @Column(name = "dropoff_address"))
    })
    private RideLocation destination;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    // Avaliações (1 a 5 estrelas)
    @Column(name = "passenger_rating")
    private Integer passengerRating;

    @Column(name = "driver_rating")
    private Integer driverRating;

    // Timestamps do ciclo de vida da corrida
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancellation_reason", length = 30)
    private CancellationReason cancellationReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // MÉTODOS AUXILIARES

    public boolean canBeCancelled() {
        return status == RideStatus.REQUESTED
                || status == RideStatus.MATCHED
                || status == RideStatus.ACCEPTED;
    }

    public boolean isActive() {
        return status == RideStatus.REQUESTED
                || status == RideStatus.MATCHED
                || status == RideStatus.ACCEPTED
                || status == RideStatus.IN_PROGRESS;
    }
}
