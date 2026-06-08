package com.uberclone.ride_service.dto.request;

import com.uberclone.ride_service.enums.CancellationReason;
import com.uberclone.ride_service.enums.RideStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideStatusUpdateDTO {

    @NotNull(message = "Novo status é obrigatório")
    private RideStatus status;

    // Só obrigatório quando status = CANCELLED
    private CancellationReason cancellationReason;

    // ID do motorista que está fazendo a ação
    @NotNull(message = "ID do motorista é obrigatório")
    private Long driverId;
}
