package com.uberclone.user_service.exception;

import com.uberclone.user_service.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;


//Handler global para tratamento de todas exeções
@RestControllerAdvice //Anotação para indicar que esta classe é um handler global de exceções para controladores REST
@Slf4j //Anotação do Lombok para adicionar um logger à classe, permitindo registrar mensagens de log para depuração e monitoramento

public class GlobalExceptionHandler {

    // Tratamento para erros de validação (@Valid nos DTOs)
    //HTTP 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.error("Validation error: {}", ex.getMessage());

        // Extrai os erros de validação
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponseDTO error = ErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Erro de validação dos dados enviados",
                request.getRequestURI(),
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


        /**
         * Tratamento para IllegalArgumentException
         * HTTP 400 - Bad Request
         * Use esta no service: throw new IllegalArgumentException("Mensagem customizada");
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
                IllegalArgumentException ex,
                HttpServletRequest request) {

            log.error("IllegalArgumentException: {}", ex.getMessage());

            ErrorResponseDTO error = ErrorResponseDTO.of(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    ex.getMessage(),
                    request.getRequestURI()
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * Tratamento para IllegalStateException
         * HTTP 409 - Conflict
         * Use esta no service: throw new IllegalStateException("Email já existe");
         */
        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ErrorResponseDTO> handleIllegalStateException(
                IllegalStateException ex,
                HttpServletRequest request) {

            log.error("IllegalStateException: {}", ex.getMessage());

            ErrorResponseDTO error = ErrorResponseDTO.of(
                    HttpStatus.CONFLICT.value(),
                    "Conflict",
                    ex.getMessage(),
                    request.getRequestURI()
            );

            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        /**
         * Tratamento para RuntimeException genérica
         * HTTP 500 - Internal Server Error
         */
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponseDTO> handleRuntimeException(
                RuntimeException ex,
                HttpServletRequest request) {

            log.error("RuntimeException: {}", ex.getMessage(), ex);

            ErrorResponseDTO error = ErrorResponseDTO.of(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    ex.getMessage(),
                    request.getRequestURI()
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        /**
         * Tratamento para TODAS as outras exceções não tratadas
         * HTTP 500 - Internal Server Error
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDTO> handleGenericException(
                Exception ex,
                HttpServletRequest request) {

            log.error("Unexpected exception: ", ex);

            ErrorResponseDTO error = ErrorResponseDTO.of(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "Ocorreu um erro interno no servidor. Tente novamente mais tarde.",
                    request.getRequestURI()
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

