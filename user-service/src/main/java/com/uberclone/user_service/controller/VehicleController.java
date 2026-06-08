package com.uberclone.user_service.controller;


import com.uberclone.user_service.dto.request.AddVehicleRequestDTO;
import com.uberclone.user_service.dto.response.VehicleResponseDTO;
import com.uberclone.user_service.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller para operações de veículos
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleService vehicleService;

    // POST /api/vehicles/driver/{driverId}
    // Adiciona veículo ao motorista
    @PostMapping("/driver/{driverId}")
    public ResponseEntity<VehicleResponseDTO> addVehicle(
            @PathVariable Long driverId,
            @Valid @RequestBody AddVehicleRequestDTO dto) {

        log.info("POST /api/vehicles/driver/{}", driverId);

        VehicleResponseDTO response = vehicleService.addVehicle(driverId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/vehicles/{id}
    // Busca veículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable Long id) {
        log.info("GET /api/vehicles/{}", id);

        VehicleResponseDTO response = vehicleService.findById(id);

        return ResponseEntity.ok(response);
    }

    // GET /api/vehicles/driver/{driverId}
    // Busca veículos do motorista
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<VehicleResponseDTO>> getVehiclesByDriverId(
            @PathVariable Long driverId,
            @RequestParam(required = false) Boolean activeOnly) {

        log.info("GET /api/vehicles/driver/{} - activeOnly: {}", driverId, activeOnly);

        List<VehicleResponseDTO> response;

        if (activeOnly != null && activeOnly) {
            response = vehicleService.findActiveVehiclesByDriverId(driverId);
        } else {
            response = vehicleService.findByDriverId(driverId);
        }

        return ResponseEntity.ok(response);
    }

    // PATCH /api/vehicles/{id}/activate
    // Ativa veículo
    @PatchMapping("/{id}/activate")
    public ResponseEntity<VehicleResponseDTO> activateVehicle(@PathVariable Long id) {
        log.info("PATCH /api/vehicles/{}/activate", id);

        VehicleResponseDTO response = vehicleService.activateVehicle(id);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/vehicles/{id}/deactivate
    // Desativa veículo
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<VehicleResponseDTO> deactivateVehicle(@PathVariable Long id) {
        log.info("PATCH /api/vehicles/{}/deactivate", id);

        VehicleResponseDTO response = vehicleService.deactivateVehicle(id);

        return ResponseEntity.ok(response);
    }

    // DELETE /api/vehicles/{id}
    // Deleta veículo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        log.info("DELETE /api/vehicles/{}", id);

        vehicleService.deleteVehicle(id);

        return ResponseEntity.noContent().build();
    }
}
