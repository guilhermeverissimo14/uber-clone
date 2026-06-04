package com.uberclone.user_service.dto.response;

import com.uberclone.user_service.entity.Passenger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO para resposta de passageiro
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerResponseDTO {

    private Long id;
    private UserResponseDTO user;
    private String preferences;
    private LocalDateTime createdAt;

    //Converte Entity para DTO
    public static PassengerResponseDTO fromEntity(Passenger passenger) {
        return PassengerResponseDTO.builder()
                .id(passenger.getId())
                .user(UserResponseDTO.fromEntity(passenger.getUser()))
                .preferences(passenger.getPreferences())
                .createdAt(passenger.getCreatedAt())
                .build();
    }
}
