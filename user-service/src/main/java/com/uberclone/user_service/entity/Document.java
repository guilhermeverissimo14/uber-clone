package com.uberclone.user_service.entity;


import com.uberclone.user_service.enums.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

//Documentos enviados pelos usuários para verificação (CNH, RG, etc)
@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 30)
    private DocumentType documentType;

    @Column(name = "document_number", length = 50)
    private String documentNumber;

    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // MÉTODOS AUXILIARES

    // Marca documento como verificado
    public void verify() {
        this.verified = true;
        this.verifiedAt = LocalDateTime.now();
    }

    // Marca documento como não verificado
    public void reject() {
        this.verified = false;
        this.verifiedAt = null;
    }

   // Verifica se o documento está verificado
    public boolean isVerified() {
        return verified != null && verified;
    }
}