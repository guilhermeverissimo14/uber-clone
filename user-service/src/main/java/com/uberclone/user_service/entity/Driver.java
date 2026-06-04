package com.uberclone.user_service.entity;

import com.uberclone.user_service.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Dados específicos do motorista
@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "license_category", nullable = false, length = 5)
    private String licenseCategory;

    @Column(name = "license_expires_at", nullable = false)
    private LocalDate licenseExpiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DriverStatus status = DriverStatus.PENDING;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = false;

    @Column(name = "current_latitude", precision = 10, scale = 8)
    private BigDecimal currentLatitude;

    @Column(name = "current_longitude", precision = 11, scale = 8)
    private BigDecimal currentLongitude;

    @Column(name = "last_location_at")
    private LocalDateTime lastLocationAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // RELACIONAMENTOS

    //Veículos do motorista
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Vehicle> vehicles = new ArrayList<>();

    // MÉTODOS AUXILIARES

    //Adiciona um veículo ao motorista
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        vehicle.setDriver(this);
    }

    //Remove um veículo do motorista
    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
        vehicle.setDriver(null);
    }

   //Verifica se o motorista está aprovado
    public boolean isApproved() {
        return status == DriverStatus.APPROVED;
    }

    //Verifica se o motorista pode aceitar corridas
    public boolean canAcceptRides() {
        return isApproved() && isAvailable && hasActiveVehicle();
    }

   //Verifica se possui pelo menos um veículo ativo
    public boolean hasActiveVehicle() {
        return vehicles.stream().anyMatch(Vehicle::getIsActive);
    }

    //Atualiza a localização atual do motorista
    public void updateLocation(BigDecimal latitude, BigDecimal longitude) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
        this.lastLocationAt = LocalDateTime.now();
    }

    //Define motorista como disponível
    public void goOnline() {
        if (canAcceptRides()) {
            this.isAvailable = true;
        }
    }

    //Define motorista como indisponível
    public void goOffline() {
        this.isAvailable = false;
    }
}
