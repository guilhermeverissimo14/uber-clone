package com.uberclone.user_service.service;


import com.uberclone.user_service.dto.request.AddAddressRequestDTO;
import com.uberclone.user_service.dto.response.AddressResponseDTO;
import com.uberclone.user_service.entity.Address;
import com.uberclone.user_service.entity.User;
import com.uberclone.user_service.repository.AddressRepository;
import com.uberclone.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Service responsável pela lógica de negócio de endereços
@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    // Adiciona endereço ao usuário
    @Transactional
    public AddressResponseDTO addAddress(Long userId, AddAddressRequestDTO dto) {
        log.info("Adicionando endereço ao usuário ID: {}", userId);

        // Buscar usuário
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + userId + " não encontrado"));

        // Se é o primeiro endereço ou foi marcado como default, torná-lo default
        boolean isDefault = dto.getIsDefault() != null ? dto.getIsDefault() : false;

        if (isDefault) {
            // Remove flag default dos outros endereços
            addressRepository.unsetDefaultAddressByUserId(userId);
        }

        // Criar endereço
        Address address = Address.builder()
                .user(user)
                .street(dto.getStreet())
                .number(dto.getNumber())
                .complement(dto.getComplement())
                .neighborhood(dto.getNeighborhood())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .label(dto.getLabel())
                .isDefault(isDefault)
                .build();

        address = addressRepository.save(address);
        log.info("Endereço criado com ID: {}", address.getId());

        return AddressResponseDTO.fromEntity(address);
    }

    // Busca endereço por ID
    @Transactional(readOnly = true)
    public AddressResponseDTO findById(Long id) {
        log.info("Buscando endereço por ID: {}", id);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço com ID " + id + " não encontrado"));

        return AddressResponseDTO.fromEntity(address);
    }

    // Busca endereços do usuário
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> findByUserId(Long userId) {
        log.info("Buscando endereços do usuário ID: {}", userId);

        return addressRepository.findByUserId(userId)
                .stream()
                .map(AddressResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Busca endereço padrão do usuário
    @Transactional(readOnly = true)
    public AddressResponseDTO findDefaultAddress(Long userId) {
        log.info("Buscando endereço padrão do usuário ID: {}", userId);

        Address address = addressRepository.findDefaultAddressByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não possui endereço padrão"));

        return AddressResponseDTO.fromEntity(address);
    }

    // Define endereço como padrão
    @Transactional
    public AddressResponseDTO setAsDefault(Long addressId) {
        log.info("Definindo endereço ID {} como padrão", addressId);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Endereço com ID " + addressId + " não encontrado"));

        // Remove flag default dos outros endereços do mesmo usuário
        addressRepository.unsetDefaultAddressByUserId(address.getUser().getId());

        // Define este como default
        address.setIsDefault(true);
        address = addressRepository.save(address);

        log.info("Endereço ID {} definido como padrão", addressId);

        return AddressResponseDTO.fromEntity(address);
    }

    // Deleta endereço
    @Transactional
    public void deleteAddress(Long addressId) {
        log.info("Deletando endereço ID: {}", addressId);

        if (!addressRepository.existsById(addressId)) {
            throw new RuntimeException("Endereço com ID " + addressId + " não encontrado");
        }

        addressRepository.deleteById(addressId);

        log.info("Endereço ID {} deletado", addressId);
    }
}
