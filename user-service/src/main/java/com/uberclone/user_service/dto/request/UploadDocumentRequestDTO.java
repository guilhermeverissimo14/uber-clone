package com.uberclone.user_service.dto.request;

import com.uberclone.user_service.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para upload de documento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadDocumentRequestDTO {

    @NotNull(message = "Tipo de documento é obrigatório")
    private DocumentType documentType;

    private String documentNumber;

    @NotBlank(message = "URL do arquivo é obrigatória")
    private String fileUrl;
}
