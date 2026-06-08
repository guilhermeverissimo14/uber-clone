package com.uberclone.ride_service.dto.request;

import com.uberclone.ride_service.dto.RideLocationDTO;
import com.uberclone.ride_service.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestDTO {

    @NotNull(message = "ID do passageiro é obrigatório")
    private Long passengerId;

    @NotNull(message = "Origem é obrigatória")
    @Valid
    private RideLocationDTO origin;

    @NotNull(message = "Destino é obrigatório")
    @Valid
    private RideLocationDTO destination;

    @NotNull(message = "Forma de pagamento é obrigatória")
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.CASH;
}
