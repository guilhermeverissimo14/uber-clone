package com.uberclone.user_service.entity;

import com.uberclone.user_service.enums.UserStatus;
import com.uberclone.user_service.enums.UserType;
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

// Entidade principal de usuário
// Representa tanto passageiros quanto motoristas
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(unique = true, length = 14)
    private String cpf;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "rating_avg", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal ratingAvg = BigDecimal.valueOf(5.00);

    @Column(name = "total_ratings")
    @Builder.Default
    private Integer totalRatings = 0;

    @Column(name = "profile_photo_url", columnDefinition = "TEXT")
    private String profilePhotoUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // RELACIONAMENTOS
    
    //Endereços salvos do usuário
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    //Dados de passageiro (se aplicável)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Passenger passenger;

    //Dados de motorista (se aplicável)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Driver driver;

    
    // Documentos do usuário
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    // MÉTODOS AUXILIARES

    //Adiciona um endereço ao usuário
    public void addAddress(Address address) {
        addresses.add(address);
        address.setUser(this);
    }

    //Remove um endereço do usuário
    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setUser(null);
    }

    //Adiciona um documento ao usuário
    public void addDocument(Document document) {
        documents.add(document);
        document.setUser(this);
    }

    //Remove um documento do usuário
    public void removeDocument(Document document) {
        documents.remove(document);
        document.setUser(null);
    }

   //Verifica se o usuário é passageiro
    public boolean isPassenger() {
        return userType == UserType.PASSENGER || userType == UserType.BOTH;
    }

    //Verifica se o usuário é motorista
    public boolean isDriver() {
        return userType == UserType.DRIVER || userType == UserType.BOTH;
    }

    //Verifica se o usuário está ativo
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}
