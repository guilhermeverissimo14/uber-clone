package com.uberclone.user_service.repository;

import com.uberclone.user_service.entity.Document;
import com.uberclone.user_service.entity.User;
import com.uberclone.user_service.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository para operações de banco de dados da entidade Document
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Busca documentos por ID do usuário
    List<Document> findByUserId(Long userId);

    // Busca documentos pelo objeto User
    List<Document> findByUser(User user);

    // Busca documento por tipo e ID do usuário
    Optional<Document> findByUserIdAndDocumentType(Long userId, DocumentType documentType);

    // Busca documentos verificados do usuário
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.verified = true")
    List<Document> findVerifiedDocumentsByUserId(@Param("userId") Long userId);

    // Busca documentos pendentes do usuário
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND d.verified = false")
    List<Document> findPendingDocumentsByUserId(@Param("userId") Long userId);

    // Busca documentos por tipo
    List<Document> findByDocumentType(DocumentType documentType);

    // Busca todos documentos pendentes de verificação
    @Query("SELECT d FROM Document d WHERE d.verified = false ORDER BY d.createdAt ASC")
    List<Document> findAllPendingDocuments();

    // Verifica se usuário tem documento do tipo especificado
    boolean existsByUserIdAndDocumentType(Long userId, DocumentType documentType);

    // Verifica se usuário tem documento verificado do tipo
    @Query("SELECT COUNT(d) > 0 FROM Document d " +
            "WHERE d.user.id = :userId " +
            "AND d.documentType = :documentType " +
            "AND d.verified = true")
    boolean hasVerifiedDocument(@Param("userId") Long userId, @Param("documentType") DocumentType documentType);

    // Conta documentos verificados do usuário
    @Query("SELECT COUNT(d) FROM Document d WHERE d.user.id = :userId AND d.verified = true")
    Long countVerifiedDocumentsByUserId(@Param("userId") Long userId);

    // Conta documentos pendentes
    @Query("SELECT COUNT(d) FROM Document d WHERE d.verified = false")
    Long countPendingDocuments();

    // Deleta documentos por ID do usuário
    void deleteByUserId(Long userId);
}
