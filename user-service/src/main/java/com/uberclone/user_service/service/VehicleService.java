package com.uberclone.user_service.service;

import com.uberclone.user_service.dto.request.AddVehicleRequestDTO;
import com.uberclone.user_service.dto.response.VehicleResponseDTO;
import com.uberclone.user_service.entity.Driver;
import com.uberclone.user_service.entity.Vehicle;
import com.uberclone.user_service.repository.DriverRepository;
import com.uberclone.user_service.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Service responsável pela lógica de negócio de veículos
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    // Adiciona veículo ao motorista
    @Transactional
    public VehicleResponseDTO addVehicle(Long driverId, AddVehicleRequestDTO dto) {
        log.info("Adicionando veículo ao motorista ID: {}", driverId);

        // Buscar motorista
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Motorista com ID " + driverId + " não encontrado"));

        // Verificar se placa já existe
        if (vehicleRepository.existsByLicensePlate(dto.getLicensePlate())) {
            throw new IllegalArgumentException("Placa já está cadastrada");
        }

        // Criar veículo
        Vehicle vehicle = Vehicle.builder()
                .driver(driver)
                .brand(dto.getBrand())
                .model(dto.getModel())
                .year(dto.getYear())
                .color(dto.getColor())
                .licensePlate(dto.getLicensePlate())
                .vehicleType(dto.getVehicleType())
                .seats(dto.getSeats())
                .isActive(true)
                .build();

        vehicle = vehicleRepository.save(vehicle);
        log.info("Veículo criado com ID: {}", vehicle.getId());

        return VehicleResponseDTO.fromEntity(vehicle);
    }

    // Busca veículo por ID
    @Transactional(readOnly = true)
    public VehicleResponseDTO findById(Long id) {
        log.info("Buscando veículo por ID: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo com ID " + id + " não encontrado"));

        return VehicleResponseDTO.fromEntity(vehicle);
    }

    // Busca veículos do motorista
    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> findByDriverId(Long driverId) {
        log.info("Buscando veículos do motorista ID: {}", driverId);

        return vehicleRepository.findByDriverId(driverId)
                .stream()
                .map(VehicleResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Busca veículos ativos do motorista
    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> findActiveVehiclesByDriverId(Long driverId) {
        log.info("Buscando veículos ativos do motorista ID: {}", driverId);

        return vehicleRepository.findActiveVehiclesByDriverId(driverId)
                .stream()
                .map(VehicleResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Ativa veículo
    @Transactional
    public VehicleResponseDTO activateVehicle(Long vehicleId) {
        log.info("Ativando veículo ID: {}", vehicleId);

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Veículo com ID " + vehicleId + " não encontrado"));

        vehicle.activate();
        vehicle = vehicleRepository.save(vehicle);

        log.info("Veículo ID {} ativado", vehicleId);

        return VehicleResponseDTO.fromEntity(vehicle);
    }

    // Desativa veículo
    @Transactional
    public VehicleResponseDTO deactivateVehicle(Long vehicleId) {
        log.info("Desativando veículo ID: {}", vehicleId);

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Veículo com ID " + vehicleId + " não encontrado"));

        vehicle.deactivate();
        vehicle = vehicleRepository.save(vehicle);

        log.info("Veículo ID {} desativado", vehicleId);

        return VehicleResponseDTO.fromEntity(vehicle);
    }

    // Deleta veículo
    @Transactional
    public void deleteVehicle(Long vehicleId) {
        log.info("Deletando veículo ID: {}", vehicleId);

        if (!vehicleRepository.existsById(vehicleId)) {
            throw new RuntimeException("Veículo com ID " + vehicleId + " não encontrado");
        }

        vehicleRepository.deleteById(vehicleId);

        log.info("Veículo ID {} deletado", vehicleId);
    }
}
