package com.uberclone.user_service.entity;

import com.uberclone.user_service.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

//Dados dos veículos dos motoristas
@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(nullable = false, length = 50)
    private String brand;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, length = 30)
    private String color;

    @Column(name = "license_plate", nullable = false, unique = true, length = 10)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 20)
    private VehicleType vehicleType;

    @Column(nullable = false)
    @Builder.Default
    private Integer seats = 4;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // MÉTODOS AUXILIARES

    //Retorna descrição completa do veículo
    public String getFullDescription() {
        return String.format("%s %s %d - %s (%s)",
                brand, model, year, color, licensePlate);
    }

   // Ativa o veículo
    public void activate() {
        this.isActive = true;
    }

  //Desativa o veículo
    public void deactivate() {
        this.isActive = false;
    }
}
