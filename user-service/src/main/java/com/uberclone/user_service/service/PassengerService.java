package com.uberclone.user_service.service;

import com.uberclone.user_service.dto.response.PassengerResponseDTO;
import com.uberclone.user_service.entity.Passenger;
import com.uberclone.user_service.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável pela lógica de negócio de passageiros
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerService {

    private final PassengerRepository passengerRepository;

    //Busca todos os passageiros
    @Transactional(readOnly = true)
    public List<PassengerResponseDTO> findAll() {
        log.info("Buscando todos os passageiros");

        return passengerRepository.findAll()
                .stream()
                .map(PassengerResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }


    /**
     * Busca passageiro por ID
     */
    @Transactional(readOnly = true)
    public PassengerResponseDTO findById(Long id) {
        log.info("Buscando passageiro por ID: {}", id);

        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passageiro com ID " + id + " não encontrado"));

        return PassengerResponseDTO.fromEntity(passenger);
    }

    /**
     * Busca passageiro por ID do usuário
     */
    @Transactional(readOnly = true)
    public PassengerResponseDTO findByUserId(Long userId) {
        log.info("Buscando passageiro por user ID: {}", userId);

        Passenger passenger = passengerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Passageiro para usuário ID " + userId + " não encontrado"));

        return PassengerResponseDTO.fromEntity(passenger);
    }

    /**
     * Atualiza preferências do passageiro
     */
    @Transactional
    public PassengerResponseDTO updatePreferences(Long passengerId, String preferences) {
        log.info("Atualizando preferências do passageiro ID: {}", passengerId);

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passageiro com ID " + passengerId + " não encontrado"));

        passenger.setPreferences(preferences);
        passenger = passengerRepository.save(passenger);

        log.info("Preferências do passageiro ID {} atualizadas", passengerId);

        return PassengerResponseDTO.fromEntity(passenger);
    }
}
