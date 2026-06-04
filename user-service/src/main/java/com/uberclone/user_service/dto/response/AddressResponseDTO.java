package com.uberclone.user_service.dto.response;


import com.uberclone.user_service.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO para resposta de endereço
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDTO {

    private Long id;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String label;
    private Boolean isDefault;
    private String formattedAddress;
    private LocalDateTime createdAt;

   
    public static AddressResponseDTO fromEntity(Address address) {
        return AddressResponseDTO.builder()
                .id(address.getId())
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .label(address.getLabel())
                .isDefault(address.getIsDefault())
                .formattedAddress(address.getFormattedAddress())
                .createdAt(address.getCreatedAt())
                .build();
    }
}
