package com.uberclone.ride_service.controller;

import com.uberclone.ride_service.dto.request.RideRequestDTO;
import com.uberclone.ride_service.dto.request.RideStatusUpdateDTO;
import com.uberclone.ride_service.dto.response.RideResponseDTO;
import com.uberclone.ride_service.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@Slf4j
public class RideController {

    private final RideService rideService;

    // POST /api/rides
    // Passageiro solicita uma corrida
    @PostMapping
    public ResponseEntity<RideResponseDTO> requestRide(@Valid @RequestBody RideRequestDTO dto) {
        log.info("POST /api/rides - passageiro: {}", dto.getPassengerId());

        RideResponseDTO response = rideService.requestRide(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/rides/{id}
    // Busca corrida por ID
    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDTO> getRideById(@PathVariable Long id) {
        log.info("GET /api/rides/{}", id);

        return ResponseEntity.ok(rideService.findById(id));
    }

    // GET /api/rides/passenger/{passengerId}
    // Histórico de corridas do passageiro
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<RideResponseDTO>> getRidesByPassenger(@PathVariable Long passengerId) {
        log.info("GET /api/rides/passenger/{}", passengerId);

        return ResponseEntity.ok(rideService.findByPassengerId(passengerId));
    }

    // GET /api/rides/passenger/{passengerId}/active
    // Corrida ativa do passageiro
    @GetMapping("/passenger/{passengerId}/active")
    public ResponseEntity<RideResponseDTO> getActiveRideByPassenger(@PathVariable Long passengerId) {
        log.info("GET /api/rides/passenger/{}/active", passengerId);

        return ResponseEntity.ok(rideService.getActiveRideByPassengerId(passengerId));
    }

    // GET /api/rides/driver/{driverId}
    // Histórico de corridas do motorista
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RideResponseDTO>> getRidesByDriver(@PathVariable Long driverId) {
        log.info("GET /api/rides/driver/{}", driverId);

        return ResponseEntity.ok(rideService.findByDriverId(driverId));
    }

    // PATCH /api/rides/{id}/status
    // Motorista atualiza o status da corrida (aceita, inicia, finaliza, cancela)
    @PatchMapping("/{id}/status")
    public ResponseEntity<RideResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody RideStatusUpdateDTO dto) {

        log.info("PATCH /api/rides/{}/status - novo status: {}", id, dto.getStatus());

        return ResponseEntity.ok(rideService.updateStatus(id, dto));
    }

    // PATCH /api/rides/{id}/cancel
    // Passageiro cancela a corrida
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<RideResponseDTO> cancelRide(
            @PathVariable Long id,
            @RequestParam Long passengerId) {

        log.info("PATCH /api/rides/{}/cancel - passageiro: {}", id, passengerId);

        return ResponseEntity.ok(rideService.cancelRide(id, passengerId));
    }
}

