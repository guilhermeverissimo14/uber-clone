package com.uberclone.user_service.service;


import com.uberclone.user_service.dto.request.UpdateUserRequestDTO;
import com.uberclone.user_service.dto.response.UserResponseDTO;
import com.uberclone.user_service.entity.User;
import com.uberclone.user_service.enums.UserStatus;
import com.uberclone.user_service.enums.UserType;
import com.uberclone.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


// Service responsável pela lógica de negócio de usuários
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

     // Busca usuário por ID
    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        log.info("Buscando usuário por ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado"));

        return UserResponseDTO.fromEntity(user);
    }


     // Busca usuário por email
    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        log.info("Buscando usuário por email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário com email '" + email + "' não encontrado"));

        return UserResponseDTO.fromEntity(user);
    }

    //Busca todos os usuários
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        log.info("Buscando todos os usuários");

        return userRepository.findAll()
                .stream()
                .map(UserResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    //Busca usuários por tipo
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findByUserType(UserType userType) {
        log.info("Buscando usuários por tipo: {}", userType);

        return userRepository.findByUserType(userType)
                .stream()
                .map(UserResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    //Busca usuários por status
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findByStatus(UserStatus status) {
        log.info("Buscando usuários por status: {}", status);

        return userRepository.findByStatus(status)
                .stream()
                .map(UserResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    //Atualiza dados do usuário
    @Transactional
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO dto) {
        log.info("Atualizando usuário ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado"));

        // Atualizar campos se fornecidos
        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            // Verificar se novo email já existe
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email já está cadastrado");
            }
            user.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null && !dto.getPhone().equals(user.getPhone())) {
            // Verificar se novo telefone já existe
            if (userRepository.existsByPhone(dto.getPhone())) {
                throw new IllegalArgumentException("Telefone já está cadastrado");
            }
            user.setPhone(dto.getPhone());
        }

        if (dto.getBirthDate() != null) {
            user.setBirthDate(dto.getBirthDate());
        }

        if (dto.getProfilePhotoUrl() != null) {
            user.setProfilePhotoUrl(dto.getProfilePhotoUrl());
        }

        user = userRepository.save(user);
        log.info("Usuário ID {} atualizado com sucesso", id);

        return UserResponseDTO.fromEntity(user);
    }

    //Ativa usuário
    @Transactional
    public UserResponseDTO activateUser(Long id) {
        log.info("Ativando usuário ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado"));

        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

        log.info("Usuário ID {} ativado", id);

        return UserResponseDTO.fromEntity(user);
    }

    //Desativa usuário
    @Transactional
    public UserResponseDTO deactivateUser(Long id) {
        log.info("Desativando usuário ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado"));

        user.setStatus(UserStatus.INACTIVE);
        user = userRepository.save(user);

        log.info("Usuário ID {} desativado", id);

        return UserResponseDTO.fromEntity(user);
    }

    //Suspende usuário
    @Transactional
    public UserResponseDTO suspendUser(Long id) {
        log.info("Suspendendo usuário ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado"));

        user.setStatus(UserStatus.SUSPENDED);
        user = userRepository.save(user);

        log.info("Usuário ID {} suspenso", id);

        return UserResponseDTO.fromEntity(user);
    }

    //Bane usuário permanentemente

    @Transactional
    public UserResponseDTO banUser(Long id) {
        log.info("Banindo usuário ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado"));

        user.setStatus(UserStatus.BANNED);
        user = userRepository.save(user);

        log.info("Usuário ID {} banido", id);

        return UserResponseDTO.fromEntity(user);
    }

    //Deleta usuário (soft delete - apenas inativa)

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deletando usuário ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado"));

        // Soft delete - apenas inativa
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        log.info("Usuário ID {} deletado (soft delete)", id);
    }

    //Conta total de usuários

    @Transactional(readOnly = true)
    public Long countAllUsers() {
        return userRepository.count();
    }

    //Conta usuários por tipo
    @Transactional(readOnly = true)
    public Long countByUserType(UserType userType) {
        return userRepository.countByUserType(userType);
    }
}
