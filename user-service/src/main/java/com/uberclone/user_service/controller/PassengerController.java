package com.uberclone.user_service.controller;


import com.uberclone.user_service.dto.response.PassengerResponseDTO;
import com.uberclone.user_service.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller para operações de passageiros
@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
@Slf4j
public class PassengerController {

    private final PassengerService passengerService;

    // GET /api/passengers/{id}
    // Busca passageiro por ID
    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> getPassengerById(@PathVariable Long id) {
        log.info("GET /api/passengers/{}", id);

        PassengerResponseDTO response = passengerService.findById(id);

        return ResponseEntity.ok(response);
    }

    // GET /api/passengers/user/{userId}
    // Busca passageiro por ID do usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<PassengerResponseDTO> getPassengerByUserId(@PathVariable Long userId) {
        log.info("GET /api/passengers/user/{}", userId);

        PassengerResponseDTO response = passengerService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/passengers/{id}/preferences
    // Atualiza preferências do passageiro
    @PatchMapping("/{id}/preferences")
    public ResponseEntity<PassengerResponseDTO> updatePreferences(
            @PathVariable Long id,
            @RequestBody String preferences) {

        log.info("PATCH /api/passengers/{}/preferences", id);

        PassengerResponseDTO response = passengerService.updatePreferences(id, preferences);

        return ResponseEntity.ok(response);
    }
}
