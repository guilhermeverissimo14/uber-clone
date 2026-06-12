package com.uberclone.location_service.controller;

import com.uberclone.location_service.dto.DriverLocationResponseDTO;
import com.uberclone.location_service.dto.UpdateLocationRequestDTO;
import com.uberclone.location_service.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@Slf4j
public class LocationController {

    private final LocationService locationService;

    // PUT /api/locations/driver
    // Motorista envia sua posição GPS atual
    @PutMapping("/driver")
    public ResponseEntity<Void> updateDriverLocation(
            @Valid @RequestBody UpdateLocationRequestDTO dto) {

        log.debug("PUT /api/locations/driver - motorista: {}", dto.getDriverId());
        locationService.updateDriverLocation(dto);
        return ResponseEntity.noContent().build();
    }

    // GET /api/locations/driver/{driverId}
    // Busca posição atual de um motorista
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<DriverLocationResponseDTO> getDriverLocation(
            @PathVariable Long driverId) {

        log.info("GET /api/locations/driver/{}", driverId);
        return ResponseEntity.ok(locationService.getDriverLocation(driverId));
    }

    // GET /api/locations/nearby?latitude=X&longitude=Y&radiusKm=5
    // Matching service usa esse endpoint para encontrar motoristas próximos
    @GetMapping("/nearby")
    public ResponseEntity<List<DriverLocationResponseDTO>> getNearbyDrivers(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {

        log.info("GET /api/locations/nearby - ({}, {}) raio {}km", latitude, longitude, radiusKm);
        return ResponseEntity.ok(locationService.findNearbyDrivers(latitude, longitude, radiusKm));
    }

    // DELETE /api/locations/driver/{driverId}
    // Remove motorista do mapa quando fica offline
    @DeleteMapping("/driver/{driverId}")
    public ResponseEntity<Void> removeDriverLocation(@PathVariable Long driverId) {
        log.info("DELETE /api/locations/driver/{}", driverId);
        locationService.removeDriverLocation(driverId);
        return ResponseEntity.noContent().build();
    }
}
