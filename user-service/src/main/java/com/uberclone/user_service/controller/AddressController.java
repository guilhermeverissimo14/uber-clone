package com.uberclone.user_service.controller;


import com.uberclone.user_service.dto.request.AddAddressRequestDTO;
import com.uberclone.user_service.dto.response.AddressResponseDTO;
import com.uberclone.user_service.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller para operações de endereços
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final AddressService addressService;

    // POST /api/addresses/user/{userId}
    // Adiciona endereço ao usuário
    @PostMapping("/user/{userId}")
    public ResponseEntity<AddressResponseDTO> addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddAddressRequestDTO dto) {

        log.info("POST /api/addresses/user/{}", userId);

        AddressResponseDTO response = addressService.addAddress(userId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/addresses/{id}
    // Busca endereço por ID
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Long id) {
        log.info("GET /api/addresses/{}", id);

        AddressResponseDTO response = addressService.findById(id);

        return ResponseEntity.ok(response);
    }

    // GET /api/addresses/user/{userId}
    // Busca endereços do usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByUserId(@PathVariable Long userId) {
        log.info("GET /api/addresses/user/{}", userId);

        List<AddressResponseDTO> response = addressService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }

    // GET /api/addresses/user/{userId}/default
    // Busca endereço padrão do usuário
    @GetMapping("/user/{userId}/default")
    public ResponseEntity<AddressResponseDTO> getDefaultAddress(@PathVariable Long userId) {
        log.info("GET /api/addresses/user/{}/default", userId);

        AddressResponseDTO response = addressService.findDefaultAddress(userId);

        return ResponseEntity.ok(response);
    }

    // PATCH /api/addresses/{id}/set-default
    // Define endereço como padrão
    @PatchMapping("/{id}/set-default")
    public ResponseEntity<AddressResponseDTO> setAsDefault(@PathVariable Long id) {
        log.info("PATCH /api/addresses/{}/set-default", id);

        AddressResponseDTO response = addressService.setAsDefault(id);

        return ResponseEntity.ok(response);
    }

    // DELETE /api/addresses/{id}
    // Deleta endereço
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        log.info("DELETE /api/addresses/{}", id);

        addressService.deleteAddress(id);

        return ResponseEntity.noContent().build();
    }
}
