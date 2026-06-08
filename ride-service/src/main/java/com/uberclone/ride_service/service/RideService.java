package com.uberclone.ride_service.service;

import com.uberclone.ride_service.dto.RideLocationDTO;
import com.uberclone.ride_service.dto.request.RideRequestDTO;
import com.uberclone.ride_service.dto.request.RideStatusUpdateDTO;
import com.uberclone.ride_service.dto.response.RideResponseDTO;
import com.uberclone.ride_service.entity.Ride;
import com.uberclone.ride_service.entity.RideLocation;
import com.uberclone.ride_service.enums.CancellationReason;
import com.uberclone.ride_service.enums.RideStatus;
import com.uberclone.ride_service.event.RideRequestedEvent;
import com.uberclone.ride_service.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideService {

    private final RideRepository rideRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.ride-requested}")
    private String rideRequestedTopic;

    // Passageiro solicita uma corrida
    @Transactional
    public RideResponseDTO requestRide(RideRequestDTO dto) {
        log.info("Passageiro {} solicitando corrida", dto.getPassengerId());

        // Bloqueia se o passageiro já tem uma corrida ativa
        if (rideRepository.hasActiveRide(dto.getPassengerId())) {
            throw new IllegalStateException("Passageiro já possui uma corrida ativa");
        }

        RideLocation origin = toRideLocation(dto.getOrigin());
        RideLocation destination = toRideLocation(dto.getDestination());

        Ride ride = Ride.builder()
                .passengerId(dto.getPassengerId())
                .status(RideStatus.REQUESTED)
                .origin(origin)
                .destination(destination)
                .estimatedPrice(calculateEstimatedPrice(origin, destination))
                .paymentMethod(dto.getPaymentMethod())
                .build();

        ride = rideRepository.save(ride);
        log.info("Corrida criada com ID: {}", ride.getId());

        //Publica evento de nova corrida para o Kafka (para o serviço de matching)
        publishRideRequestedEvent(ride);

        return RideResponseDTO.fromEntity(ride);
    }

    // Atualiza o status da corrida (motorista aceita, inicia, finaliza ou cancela)
    @Transactional
    public RideResponseDTO updateStatus(Long rideId, RideStatusUpdateDTO dto) {
        log.info("Atualizando corrida {} para status {}", rideId, dto.getStatus());

        Ride ride = findRideOrThrow(rideId);

        validateStatusTransition(ride.getStatus(), dto.getStatus());

        ride.setStatus(dto.getStatus());

        switch (dto.getStatus()) {
            case ACCEPTED -> {
                ride.setDriverId(dto.getDriverId());
                ride.setAcceptedAt(LocalDateTime.now());
            }
            case IN_PROGRESS -> ride.setStartedAt(LocalDateTime.now());
            case COMPLETED -> {
                ride.setCompletedAt(LocalDateTime.now());
                ride.setFinalPrice(ride.getEstimatedPrice());
            }
            case CANCELLED -> {
                ride.setCancelledAt(LocalDateTime.now());
                ride.setCancellationReason(dto.getCancellationReason());
            }
            default -> throw new IllegalArgumentException("Transição de status inválida");
        }

        ride = rideRepository.save(ride);
        log.info("Corrida {} atualizada para {}", rideId, dto.getStatus());

        return RideResponseDTO.fromEntity(ride);
    }

    // Passageiro cancela a corrida
    @Transactional
    public RideResponseDTO cancelRide(Long rideId, Long passengerId) {
        log.info("Passageiro {} cancelando corrida {}", passengerId, rideId);

        Ride ride = findRideOrThrow(rideId);

        if (!ride.getPassengerId().equals(passengerId)) {
            throw new IllegalArgumentException("Corrida não pertence a este passageiro");
        }

        if (!ride.canBeCancelled()) {
            throw new IllegalStateException("Corrida com status " + ride.getStatus() + " não pode ser cancelada");
        }

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(LocalDateTime.now());
        ride.setCancellationReason(CancellationReason.PASSENGER_CANCELLED);

        ride = rideRepository.save(ride);

        return RideResponseDTO.fromEntity(ride);
    }

    // Busca corrida por ID
    @Transactional(readOnly = true)
    public RideResponseDTO findById(Long rideId) {
        return RideResponseDTO.fromEntity(findRideOrThrow(rideId));
    }

    // Histórico de corridas do passageiro
    @Transactional(readOnly = true)
    public List<RideResponseDTO> findByPassengerId(Long passengerId) {
        log.info("Buscando corridas do passageiro {}", passengerId);
        return rideRepository.findByPassengerIdOrderByCreatedAtDesc(passengerId)
                .stream()
                .map(RideResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Histórico de corridas do motorista
    @Transactional(readOnly = true)
    public List<RideResponseDTO> findByDriverId(Long driverId) {
        log.info("Buscando corridas do motorista {}", driverId);
        return rideRepository.findByDriverIdOrderByCreatedAtDesc(driverId)
                .stream()
                .map(RideResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Corrida ativa do passageiro
    @Transactional(readOnly = true)
    public RideResponseDTO getActiveRideByPassengerId(Long passengerId) {
        Ride ride = rideRepository.findActiveRideByPassengerId(passengerId)
                .orElseThrow(() -> new RuntimeException("Nenhuma corrida ativa para o passageiro " + passengerId));
        return RideResponseDTO.fromEntity(ride);
    }

    // -------------------------
    // MÉTODOS PRIVADOS
    // -------------------------

    private Ride findRideOrThrow(Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Corrida com ID " + rideId + " não encontrada"));
    }

    private RideLocation toRideLocation(RideLocationDTO dto) {
        return RideLocation.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .address(dto.getAddress())
                .build();
    }

    private void publishRideRequestedEvent(Ride ride) {
        RideRequestedEvent event = RideRequestedEvent.builder()
                .rideId(ride.getId())
                .passengerId(ride.getPassengerId())
                .originLatitude(ride.getOrigin().getLatitude())
                .originLongitude(ride.getOrigin().getLongitude())
                .originAddress(ride.getOrigin().getAddress())
                .destinationLatitude(ride.getDestination().getLatitude())
                .destinationLongitude(ride.getDestination().getLongitude())
                .destinationAddress(ride.getDestination().getAddress())
                .build();

        kafkaTemplate.send(rideRequestedTopic, String.valueOf(ride.getId()), event);
        log.info("Evento RideRequested publicado no Kafka para corrida {}", ride.getId());
    }

    // Fórmula de Haversine simplificada: calcula distância em linha reta entre dois pontos
    private BigDecimal calculateEstimatedPrice(RideLocation origin, RideLocation destination) {
        double lat1 = origin.getLatitude().doubleValue();
        double lon1 = origin.getLongitude().doubleValue();
        double lat2 = destination.getLatitude().doubleValue();
        double lon2 = destination.getLongitude().doubleValue();

        double earthRadiusKm = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double distanceKm = earthRadiusKm * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Preço base R$ 5,00 + R$ 2,00 por km
        double price = 5.0 + (distanceKm * 2.0);

        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }

    // Garante que a transição de status faz sentido no fluxo da corrida
    private void validateStatusTransition(RideStatus current, RideStatus next) {
        boolean valid = switch (current) {
            case REQUESTED -> next == RideStatus.MATCHED || next == RideStatus.CANCELLED;
            case MATCHED   -> next == RideStatus.ACCEPTED || next == RideStatus.CANCELLED;
            case ACCEPTED  -> next == RideStatus.IN_PROGRESS || next == RideStatus.CANCELLED;
            case IN_PROGRESS -> next == RideStatus.COMPLETED || next == RideStatus.CANCELLED;
            default -> false;
        };

        if (!valid) {
            throw new IllegalStateException(
                "Transição inválida: " + current + " → " + next
            );
        }
    }
}
