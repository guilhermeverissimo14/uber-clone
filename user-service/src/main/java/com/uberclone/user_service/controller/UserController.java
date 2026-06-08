package com.uberclone.user_service.controller;


import com.uberclone.user_service.dto.request.UpdateUserRequestDTO;
import com.uberclone.user_service.dto.response.UserResponseDTO;
import com.uberclone.user_service.enums.UserStatus;
import com.uberclone.user_service.enums.UserType;
import com.uberclone.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Controller para operações de usuários
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // GET /api/users/{id}
    // Busca usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{}", id);

        UserResponseDTO response = userService.findById(id);

        return ResponseEntity.ok(response);
    }

    // GET /api/users/email/{email}
    // Busca usuário por email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        log.info("GET /api/users/email/{}", email);

        UserResponseDTO response = userService.findByEmail(email);

        return ResponseEntity.ok(response);
    }

    // GET /api/users
    // Lista todos os usuários (com filtros opcionais)
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(
            @RequestParam(required = false) UserType userType,
            @RequestParam(required = false) UserStatus status) {

        log.info("GET /api/users - userType: {}, status: {}", userType, status);

        List<UserResponseDTO> response;

        if (userType != null) {
            response = userService.findByUserType(userType);
        } else if (status != null) {
            response = userService.findByStatus(status);
        } else {
            response = userService.findAll();
        }

        return ResponseEntity.ok(response);
    }

    // PUT /api/users/{id}
    // Atualiza dados do usuário
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDTO dto) {

        log.info("PUT /api/users/{}", id);

        UserResponseDTO response = userService.updateUser(id, dto);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/users/{id}/activate
    // Ativa usuário
    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponseDTO> activateUser(@PathVariable Long id) {
        log.info("PATCH /api/users/{}/activate", id);

        UserResponseDTO response = userService.activateUser(id);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/users/{id}/deactivate
    // Desativa usuário
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponseDTO> deactivateUser(@PathVariable Long id) {
        log.info("PATCH /api/users/{}/deactivate", id);

        UserResponseDTO response = userService.deactivateUser(id);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/users/{id}/suspend
    // Suspende usuário
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<UserResponseDTO> suspendUser(@PathVariable Long id) {
        log.info("PATCH /api/users/{}/suspend", id);

        UserResponseDTO response = userService.suspendUser(id);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/users/{id}/ban
    // Bane usuário
    @PatchMapping("/{id}/ban")
    public ResponseEntity<UserResponseDTO> banUser(@PathVariable Long id) {
        log.info("PATCH /api/users/{}/ban", id);

        UserResponseDTO response = userService.banUser(id);

        return ResponseEntity.ok(response);
    }

    // DELETE /api/users/{id}
    // Deleta usuário (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{}", id);

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    // GET /api/users/stats
    // Retorna estatísticas de usuários
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getUserStats() {
        log.info("GET /api/users/stats");

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", userService.countAllUsers());
        stats.put("passengers", userService.countByUserType(UserType.PASSENGER));
        stats.put("drivers", userService.countByUserType(UserType.DRIVER));
        stats.put("both", userService.countByUserType(UserType.BOTH));

        return ResponseEntity.ok(stats);
    }
}
