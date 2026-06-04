package com.uberclone.user_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Endereços salvos dos usuários (Ex: casa, trabalho, etc)

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Chave primária auto-incrementada
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)    // Repressenta uma chave estrangeira para a tabela de usuários
    private User user;

    @Column(nullable = false, length = 255)
    private String street;

    @Column(length = 20)
    private String number;

    @Column(length = 255)
    private String complement;

    @Column(length = 100)
    private String neighborhood;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(length = 50)
    private String label; // HOME, WORK, OTHER

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    //Metodos auxiliares
    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(street);
        if (number != null && !number.isEmpty()) {
            sb.append(", ").append(number);
        }
        if (complement != null && !complement.isEmpty()) {
            sb.append(" (").append(complement).append(")");
        }
        if (neighborhood != null && !neighborhood.isEmpty()) {
            sb.append(", ").append(neighborhood);
        }
        sb.append(", ").append(city);
        sb.append(", ").append(state);
        if (zipCode != null && !zipCode.isEmpty()) {
            sb.append(" - ").append(zipCode);
        }
        return sb.toString();
    }

}
