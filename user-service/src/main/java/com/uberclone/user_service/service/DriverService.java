package com.uberclone.user_service.service;

import com.uberclone.user_service.dto.request.RegisterDriverRequestDTO;
import com.uberclone.user_service.dto.request.UpdateDriverLocationRequestDTO;
import com.uberclone.user_service.dto.response.DriverResponseDTO;
import com.uberclone.user_service.entity.Driver;
import com.uberclone.user_service.entity.User;
import com.uberclone.user_service.enums.DriverStatus;
import com.uberclone.user_service.enums.UserType;
import com.uberclone.user_service.repository.DriverRepository;
import com.uberclone.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

// Service responsável pela lógica de negócio de motoristas
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    // Registra um novo motorista
    @Transactional
    public DriverResponseDTO registerDriver(Long userId, RegisterDriverRequestDTO dto) {
        log.info("Registrando motorista para usuário ID: {}", userId);

        // Buscar usuário
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + userId + " não encontrado"));

        // Verificar se usuário já é motorista
        if (driverRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Usuário já está cadastrado como motorista");
        }

        // Verificar se CNH já existe
        if (driverRepository.existsByLicenseNumber(dto.getLicenseNumber())) {
            throw new IllegalArgumentException("CNH já está cadastrada");
        }

        // Atualizar tipo do usuário se necessário
        if (user.getUserType() == UserType.PASSENGER) {
            user.setUserType(UserType.BOTH);
            userRepository.save(user);
        }

        // Criar motorista
        Driver driver = Driver.builder()
                .user(user)
                .licenseNumber(dto.getLicenseNumber())
                .licenseCategory(dto.getLicenseCategory())
                .licenseExpiresAt(dto.getLicenseExpiresAt())
                .status(DriverStatus.PENDING)
                .isAvailable(false)
                .build();

        driver = driverRepository.save(driver);
        log.info("Motorista criado com ID: {}", driver.getId());

        // TODO: Publicar evento no Kafka (DriverCreatedEvent)

        return DriverResponseDTO.fromEntity(driver);
    }

    // Busca motorista por ID
    @Transactional(readOnly = true)
    public DriverResponseDTO findById(Long id) {
        log.info("Buscando motorista por ID: {}", id);

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Motorista com ID " + id + " não encontrado"));

        return DriverResponseDTO.fromEntity(driver);
    }

    // Busca motorista por ID do usuário
    @Transactional(readOnly = true)
    public DriverResponseDTO findByUserId(Long userId) {
        log.info("Buscando motorista por user ID: {}", userId);

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Motorista para usuário ID " + userId + " não encontrado"));

        return DriverResponseDTO.fromEntity(driver);
    }

    // Busca todos os motoristas
    @Transactional(readOnly = true)
    public List<DriverResponseDTO> findAll() {
        log.info("Buscando todos os motoristas");

        return driverRepository.findAll()
                .stream()
                .map(DriverResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Busca motoristas por status
    @Transactional(readOnly = true)
    public List<DriverResponseDTO> findByStatus(DriverStatus status) {
        log.info("Buscando motoristas por status: {}", status);

        return driverRepository.findByStatus(status)
                .stream()
                .map(DriverResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Busca motoristas disponíveis
    @Transactional(readOnly = true)
    public List<DriverResponseDTO> findAvailableDrivers() {
        log.info("Buscando motoristas disponíveis");

        return driverRepository.findAvailableDrivers()
                .stream()
                .map(DriverResponseDTO::fromEntityWithoutVehicles)
                .collect(Collectors.toList());
    }

    // Busca motoristas próximos
    @Transactional(readOnly = true)
    public List<DriverResponseDTO> findNearbyDrivers(BigDecimal latitude, BigDecimal longitude, Double radiusKm) {
        log.info("Buscando motoristas próximos de ({}, {}) em raio de {}km", latitude, longitude, radiusKm);

        return driverRepository.findNearbyDrivers(latitude, longitude, radiusKm)
                .stream()
                .map(DriverResponseDTO::fromEntityWithoutVehicles)
                .collect(Collectors.toList());
    }

    // Aprova motorista
    @Transactional
    public DriverResponseDTO approveDriver(Long driverId) {
        log.info("Aprovando motorista ID: {}", driverId);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Motorista com ID " + driverId + " não encontrado"));

        driver.setStatus(DriverStatus.APPROVED);
        driver = driverRepository.save(driver);

        log.info("Motorista ID {} aprovado", driverId);

        // TODO: Publicar evento no Kafka (DriverApprovedEvent)
        // TODO: Enviar notificação para o motorista

        return DriverResponseDTO.fromEntity(driver);
    }

    // Rejeita motorista
    @Transactional
    public DriverResponseDTO rejectDriver(Long driverId) {
        log.info("Rejeitando motorista ID: {}", driverId);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Motorista com ID " + driverId + " não encontrado"));

        driver.setStatus(DriverStatus.REJECTED);
        driver = driverRepository.save(driver);

        log.info("Motorista ID {} rejeitado", driverId);

        // TODO: Publicar evento no Kafka (DriverRejectedEvent)
        // TODO: Enviar notificação para o motorista

        return DriverResponseDTO.fromEntity(driver);
    }

    // Suspende motorista
    @Transactional
    public DriverResponseDTO suspendDriver(Long driverId) {
        log.info("Suspendendo motorista ID: {}", driverId);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Motorista com ID " + driverId + " não encontrado"));

        driver.setStatus(DriverStatus.SUSPENDED);
        driver.setIsAvailable(false); // Força offline
        driver = driverRepository.save(driver);

        log.info("Motorista ID {} suspenso", driverId);

        return DriverResponseDTO.fromEntity(driver);
    }

    // Coloca motorista online (disponível)
    @Transactional
    public DriverResponseDTO goOnline(Long driverId) {
        log.info("Colocando motorista ID {} online", driverId);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Motorista com ID " + driverId + " não encontrado"));

        // Validar se motorista está aprovado
        if (driver.getStatus() != DriverStatus.APPROVED) {
            throw new IllegalStateException("Motorista precisa estar aprovado para ficar online");
        }

        // Validar se tem veículo ativo
        if (!driver.hasActiveVehicle()) {
            throw new IllegalStateException("Motorista precisa ter um veículo ativo para ficar online");
        }

        driver.setIsAvailable(true);
        driver = driverRepository.save(driver);

        log.info("Motorista ID {} está online", driverId);

        // TODO: Publicar evento no Kafka (DriverWentOnlineEvent)

        return DriverResponseDTO.fromEntityWithoutVehicles(driver);
    }

    // Coloca motorista offline (indisponível)
    @Transactional
    public DriverResponseDTO goOffline(Long driverId) {
        log.info("Colocando motorista ID {} offline", driverId);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Motorista com ID " + driverId + " não encontrado"));

        driver.setIsAvailable(false);
        driver = driverRepository.save(driver);

        log.info("Motorista ID {} está offline", driverId);

        // TODO: Publicar evento no Kafka (DriverWentOfflineEvent)

        return DriverResponseDTO.fromEntityWithoutVehicles(driver);
    }

    // Atualiza localização do motorista
    @Transactional
    public void updateLocation(Long driverId, UpdateDriverLocationRequestDTO dto) {
        log.debug("Atualizando localização do motorista ID: {}", driverId);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Motorista com ID " + driverId + " não encontrado"));

        driver.updateLocation(dto.getLatitude(), dto.getLongitude());
        driverRepository.save(driver);

        // TODO: Publicar evento no Kafka (DriverLocationUpdatedEvent)
        // Este evento será consumido pelo Location Service para atualizar cache no Redis
    }

    // Conta total de motoristas
    @Transactional(readOnly = true)
    public Long countAllDrivers() {
        return driverRepository.count();
    }

    // Conta motoristas por status
    @Transactional(readOnly = true)
    public Long countByStatus(DriverStatus status) {
        return driverRepository.countByStatus(status);
    }

    // Conta motoristas disponíveis
    @Transactional(readOnly = true)
    public Long countAvailableDrivers() {
        return driverRepository.countAvailableDrivers();
    }
}
