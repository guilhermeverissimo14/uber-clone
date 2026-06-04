package com.uberclone.user_service.dto.response;

import com.uberclone.user_service.entity.User;
import com.uberclone.user_service.enums.UserStatus;
import com.uberclone.user_service.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO para resposta para o usuário
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String cpf;
    private LocalDate birthDate;
    private UserType userType;
    private UserStatus status;
    private BigDecimal ratingAvg;
    private Integer totalRatings;
    private String profilePhotoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Converte entidade para DTO
    public static UserResponseDTO fromEntity(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .cpf(user.getCpf())
                .birthDate(user.getBirthDate())
                .userType(user.getUserType())
                .status(user.getStatus())
                .ratingAvg(user.getRatingAvg())
                .totalRatings(user.getTotalRatings())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
