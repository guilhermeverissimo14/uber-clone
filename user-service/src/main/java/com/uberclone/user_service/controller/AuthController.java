package com.uberclone.user_service.controller;

import com.uberclone.user_service.dto.request.LoginRequestDTO;
import com.uberclone.user_service.dto.request.RegisterUserRequestDTO;
import com.uberclone.user_service.dto.response.AuthResponseDTO;
import com.uberclone.user_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller para autenticação (login e registro)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login
    // Realiza login do usuário
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        log.info("POST /api/auth/login - Email: {}", dto.getEmail());

        AuthResponseDTO response = authService.login(dto);

        return ResponseEntity.ok(response);
    }

    // POST /api/auth/register
    // Registra novo usuário
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterUserRequestDTO dto) {
        log.info("POST /api/auth/register - Email: {}", dto.getEmail());

        AuthResponseDTO response = authService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/auth/health
    // Health check do serviço de autenticação
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }
}
