package com.uberclone.ride_service.enums;

public enum RideStatus {
     REQUESTED,   // Passageiro solicitou, aguardando motorista
    MATCHED,     // Motorista encontrado, aguardando aceite
    ACCEPTED,    // Motorista aceitou, indo buscar passageiro
    IN_PROGRESS, // Corrida em andamento
    COMPLETED,   // Corrida finalizada com sucesso
    CANCELLED    // Corrida cancelada (por qualquer motivo)
}
