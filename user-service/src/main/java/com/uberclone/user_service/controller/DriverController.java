package com.uberclone.user_service.controller;

import com.uberclone.user_service.dto.request.RegisterDriverRequestDTO;
import com.uberclone.user_service.dto.request.UpdateDriverLocationRequestDTO;
import com.uberclone.user_service.dto.response.DriverResponseDTO;
import com.uberclone.user_service.enums.DriverStatus;
import com.uberclone.user_service.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Controller para operações de motoristas
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
public class DriverController {

    private final DriverService driverService;

    // POST /api/drivers/register/{userId}
    // Registra usuário como motorista
    @PostMapping("/register/{userId}")
    public ResponseEntity<DriverResponseDTO> registerDriver(
            @PathVariable Long userId,
            @Valid @RequestBody RegisterDriverRequestDTO dto) {

        log.info("POST /api/drivers/register/{}", userId);

        DriverResponseDTO response = driverService.registerDriver(userId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/drivers/{id}
    // Busca motorista por ID
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getDriverById(@PathVariable Long id) {
        log.info("GET /api/drivers/{}", id);

        DriverResponseDTO response = driverService.findById(id);

        return ResponseEntity.ok(response);
    }

    // GET /api/drivers/user/{userId}
    // Busca motorista por ID do usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<DriverResponseDTO> getDriverByUserId(@PathVariable Long userId) {
        log.info("GET /api/drivers/user/{}", userId);

        DriverResponseDTO response = driverService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }

    // GET /api/drivers
    // Lista motoristas (com filtros opcionais)
    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getAllDrivers(
            @RequestParam(required = false) DriverStatus status,
            @RequestParam(required = false) Boolean available) {

        log.info("GET /api/drivers - status: {}, available: {}", status, available);

        List<DriverResponseDTO> response;

        if (status != null) {
            response = driverService.findByStatus(status);
        } else if (available != null && available) {
            response = driverService.findAvailableDrivers();
        } else {
            response = driverService.findAll();
        }

        return ResponseEntity.ok(response);
    }

    // GET /api/drivers/nearby
    // Busca motoristas próximos
    @GetMapping("/nearby")
    public ResponseEntity<List<DriverResponseDTO>> getNearbyDrivers(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {

        log.info("GET /api/drivers/nearby - lat: {}, lng: {}, radius: {}km",
                latitude, longitude, radiusKm);

        List<DriverResponseDTO> response = driverService.findNearbyDrivers(
                latitude, longitude, radiusKm);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/drivers/{id}/approve
    // Aprova motorista
    @PatchMapping("/{id}/approve")
    public ResponseEntity<DriverResponseDTO> approveDriver(@PathVariable Long id) {
        log.info("PATCH /api/drivers/{}/approve", id);

        DriverResponseDTO response = driverService.approveDriver(id);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/drivers/{id}/reject
    // Rejeita motorista
    @PatchMapping("/{id}/reject")
    public ResponseEntity<DriverResponseDTO> rejectDriver(@PathVariable Long id) {
        log.info("PATCH /api/drivers/{}/reject", id);

        DriverResponseDTO response = driverService.rejectDriver(id);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/drivers/{id}/suspend
    // Suspende motorista
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<DriverResponseDTO> suspendDriver(@PathVariable Long id) {
        log.info("PATCH /api/drivers/{}/suspend", id);

        DriverResponseDTO response = driverService.suspendDriver(id);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/drivers/{id}/go-online
    // Coloca motorista online (disponível)
    @PatchMapping("/{id}/go-online")
    public ResponseEntity<DriverResponseDTO> goOnline(@PathVariable Long id) {
        log.info("PATCH /api/drivers/{}/go-online", id);

        DriverResponseDTO response = driverService.goOnline(id);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/drivers/{id}/go-offline
    // Coloca motorista offline (indisponível)
    @PatchMapping("/{id}/go-offline")
    public ResponseEntity<DriverResponseDTO> goOffline(@PathVariable Long id) {
        log.info("PATCH /api/drivers/{}/go-offline", id);

        DriverResponseDTO response = driverService.goOffline(id);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/drivers/{id}/location
    // Atualiza localização do motorista
    @PatchMapping("/{id}/location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDriverLocationRequestDTO dto) {

        log.debug("PATCH /api/drivers/{}/location", id);

        driverService.updateLocation(id, dto);

        return ResponseEntity.noContent().build();
    }

    // GET /api/drivers/stats
    // Retorna estatísticas de motoristas
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getDriverStats() {
        log.info("GET /api/drivers/stats");

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", driverService.countAllDrivers());
        stats.put("pending", driverService.countByStatus(DriverStatus.PENDING));
        stats.put("approved", driverService.countByStatus(DriverStatus.APPROVED));
        stats.put("rejected", driverService.countByStatus(DriverStatus.REJECTED));
        stats.put("suspended", driverService.countByStatus(DriverStatus.SUSPENDED));
        stats.put("available", driverService.countAvailableDrivers());

        return ResponseEntity.ok(stats);
    }
}
