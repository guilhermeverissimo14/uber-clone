package com.uberclone.user_service.service;

import com.uberclone.user_service.dto.request.LoginRequestDTO;
import com.uberclone.user_service.dto.request.RegisterUserRequestDTO;
import com.uberclone.user_service.dto.response.AuthResponseDTO;
import com.uberclone.user_service.dto.response.UserResponseDTO;
import com.uberclone.user_service.entity.Passenger;
import com.uberclone.user_service.entity.User;
import com.uberclone.user_service.enums.UserStatus;
import com.uberclone.user_service.enums.UserType;
import com.uberclone.user_service.repository.PassengerRepository;
import com.uberclone.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Service responsável por autenticação e registro de usuários
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    // TODO: Adicionar JwtTokenProvider quando criar a classe de segurança

    // Método para login de usuário
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO dto) {
        log.info("Tentativa de login para email: {}", dto.getEmail());

        // Buscar usuário por email
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou senha inválidos"));

        // Verificar senha
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email ou senha inválidos");
        }

        // Verificar se usuário está ativo
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("Usuário inativo. Entre em contato com o suporte.");
        }

        log.info("Login bem-sucedido para usuário ID: {}", user.getId());

        // TODO: Gerar JWT token
        String token = "fake-jwt-token-" + user.getId(); // Temporário
        Long expiresIn = 86400000L; // 24 horas

        UserResponseDTO userResponse = UserResponseDTO.fromEntity(user);

        return AuthResponseDTO.of(token, expiresIn, userResponse);
    }

    // Método para registro de usuário
    @Transactional
    public AuthResponseDTO register(RegisterUserRequestDTO dto) {
        log.info("Iniciando registro de usuário: {}", dto.getEmail());

        // Validar se email já existe
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já está cadastrado");
        }

        // Validar se telefone já existe
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("Telefone já está cadastrado");
        }

        // Validar se CPF já existe (se informado)
        if (dto.getCpf() != null && userRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF já está cadastrado");
        }

        // Criar usuário
        User user = User.builder()
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .cpf(dto.getCpf())
                .birthDate(dto.getBirthDate())
                .userType(dto.getUserType())
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("Usuário criado com ID: {}", user.getId());

        // Se for passageiro, criar registro de passageiro
        if (dto.getUserType() == UserType.PASSENGER || dto.getUserType() == UserType.BOTH) {
            Passenger passenger = Passenger.builder()
                    .user(user)
                    .build();
            passengerRepository.save(passenger);
            log.info("Passageiro criado para usuário ID: {}", user.getId());
        }

        // TODO: Publicar evento no Kafka (UserCreatedEvent)

        // Gerar token e retornar
        String token = "fake-jwt-token-" + user.getId(); // Temporário
        Long expiresIn = 86400000L;

        UserResponseDTO userResponse = UserResponseDTO.fromEntity(user);

        log.info("Registro concluído para usuário ID: {}", user.getId());

        return AuthResponseDTO.of(token, expiresIn, userResponse);
    }
}
