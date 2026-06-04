package com.uberclone.user_service.dto.response;


import com.uberclone.user_service.entity.Document;
import com.uberclone.user_service.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO para resposta de documento
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDTO {

    private Long id;
    private DocumentType documentType;
    private String documentNumber;
    private String fileUrl;
    private Boolean verified;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;

    public static DocumentResponseDTO fromEntity(Document document) {
        return DocumentResponseDTO.builder()
                .id(document.getId())
                .documentType(document.getDocumentType())
                .documentNumber(document.getDocumentNumber())
                .fileUrl(document.getFileUrl())
                .verified(document.getVerified())
                .verifiedAt(document.getVerifiedAt())
                .createdAt(document.getCreatedAt())
                .build();
    }
}
