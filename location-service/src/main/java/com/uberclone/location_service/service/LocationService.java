package com.uberclone.location_service.service;

import com.uberclone.location_service.dto.DriverLocationResponseDTO;
import com.uberclone.location_service.dto.UpdateLocationRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private static final String DRIVERS_GEO_KEY = "drivers:locations";

    private final RedisTemplate<String, String> redisTemplate;

    // Motorista atualiza sua localização no Redis
    public void updateDriverLocation(UpdateLocationRequestDTO dto) {
        log.debug("Atualizando localização do motorista {}: ({}, {})",
                dto.getDriverId(), dto.getLatitude(), dto.getLongitude());

        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

        Point point = new Point(
                dto.getLongitude().doubleValue(), // Redis usa longitude primeiro
                dto.getLatitude().doubleValue()
        );

        geoOps.add(DRIVERS_GEO_KEY, point, String.valueOf(dto.getDriverId()));

        log.debug("Localização do motorista {} atualizada no Redis", dto.getDriverId());
    }

    // Busca motoristas dentro de um raio a partir de um ponto
    public List<DriverLocationResponseDTO> findNearbyDrivers(
            Double latitude, Double longitude, Double radiusKm) {

        log.info("Buscando motoristas em raio de {}km de ({}, {})", radiusKm, latitude, longitude);

        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

        Circle circle = new Circle(
                new Point(longitude, latitude), // longitude primeiro no Redis
                new Distance(radiusKm, Metrics.KILOMETERS)
        );

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(20); // máximo 20 motoristas por busca

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                geoOps.radius(DRIVERS_GEO_KEY, circle, args);

        if (results == null) {
            return Collections.emptyList();
        }

        return results.getContent().stream()
                .map(result -> {
                    Long driverId = Long.valueOf(result.getContent().getName());
                    Point pos = result.getContent().getPoint();
                    Double distance = result.getDistance().getValue();

                    return DriverLocationResponseDTO.builder()
                            .driverId(driverId)
                            .latitude(pos.getY())   // Y = latitude
                            .longitude(pos.getX())  // X = longitude
                            .distanceKm(distance)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Busca a localização atual de um motorista específico
    public DriverLocationResponseDTO getDriverLocation(Long driverId) {
        log.info("Buscando localização do motorista {}", driverId);

        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

        List<Point> positions = geoOps.position(DRIVERS_GEO_KEY, String.valueOf(driverId));

        if (positions == null || positions.isEmpty() || positions.get(0) == null) {
            throw new RuntimeException("Localização do motorista " + driverId + " não encontrada");
        }

        Point point = positions.get(0);

        return DriverLocationResponseDTO.builder()
                .driverId(driverId)
                .latitude(point.getY())
                .longitude(point.getX())
                .build();
    }

    // Remove motorista do mapa (quando fica offline)
    public void removeDriverLocation(Long driverId) {
        log.info("Removendo localização do motorista {}", driverId);
        redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY, String.valueOf(driverId));
    }
}
