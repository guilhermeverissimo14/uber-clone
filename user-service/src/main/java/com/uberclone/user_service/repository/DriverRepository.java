package com.uberclone.user_service.repository;

import com.uberclone.user_service.entity.Driver;
import com.uberclone.user_service.entity.User;
import com.uberclone.user_service.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados da entidade Driver
 */
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    /**
     * Busca motorista por ID do usuário
     */
    Optional<Driver> findByUserId(Long userId);

    /**
     * Busca motorista pelo objeto User
     */
    Optional<Driver> findByUser(User user);

    /**
     * Busca motorista por número da CNH
     */
    Optional<Driver> findByLicenseNumber(String licenseNumber);

    /**
     * Verifica se existe motorista para o usuário
     */
    boolean existsByUserId(Long userId);

    /**
     * Verifica se existe motorista com o número da CNH
     */
    boolean existsByLicenseNumber(String licenseNumber);

    /**
     * Busca motoristas por status
     */
    List<Driver> findByStatus(DriverStatus status);

    /**
     * Busca motoristas disponíveis (online)
     */
    @Query("SELECT d FROM Driver d WHERE d.isAvailable = true AND d.status = 'APPROVED'")
    List<Driver> findAvailableDrivers();

    /**
     * Busca motoristas próximos (raio em km)
     * Fórmula de Haversine simplificada
     */
    @Query(value = "SELECT * FROM drivers d " +
            "WHERE d.is_available = true " +
            "AND d.status = 'APPROVED' " +
            "AND d.current_latitude IS NOT NULL " +
            "AND d.current_longitude IS NOT NULL " +
            "AND ( " +
            "  6371 * acos( " +
            "    cos(radians(:latitude)) * " +
            "    cos(radians(d.current_latitude)) * " +
            "    cos(radians(d.current_longitude) - radians(:longitude)) + " +
            "    sin(radians(:latitude)) * " +
            "    sin(radians(d.current_latitude)) " +
            "  ) " +
            ") <= :radiusKm " +
            "ORDER BY ( " +
            "  6371 * acos( " +
            "    cos(radians(:latitude)) * " +
            "    cos(radians(d.current_latitude)) * " +
            "    cos(radians(d.current_longitude) - radians(:longitude)) + " +
            "    sin(radians(:latitude)) * " +
            "    sin(radians(d.current_latitude)) " +
            "  ) " +
            ")",
            nativeQuery = true)
    List<Driver> findNearbyDrivers(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusKm") Double radiusKm
    );

    /**
     * Busca motoristas aprovados
     */
    @Query("SELECT d FROM Driver d WHERE d.status = 'APPROVED'")
    List<Driver> findApprovedDrivers();

    /**
     * Busca motoristas pendentes de aprovação
     */
    @Query("SELECT d FROM Driver d WHERE d.status = 'PENDING'")
    List<Driver> findPendingDrivers();

    /**
     * Busca motorista com todos os relacionamentos (eager)
     */
    @Query("SELECT d FROM Driver d " +
            "LEFT JOIN FETCH d.user " +
            "LEFT JOIN FETCH d.vehicles " +
            "WHERE d.id = :id")
    Optional<Driver> findByIdWithRelationships(@Param("id") Long id);

    /**
     * Busca motoristas inativos há mais de X minutos
     */
    @Query("SELECT d FROM Driver d " +
            "WHERE d.isAvailable = true " +
            "AND d.lastLocationAt < :threshold")
    List<Driver> findInactiveDrivers(@Param("threshold") LocalDateTime threshold);

    /**
     * Conta motoristas por status
     */
    Long countByStatus(DriverStatus status);

    /**
     * Conta motoristas disponíveis
     */
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.isAvailable = true AND d.status = 'APPROVED'")
    Long countAvailableDrivers();

    /**
     * Deleta motorista por ID do usuário
     */
    void deleteByUserId(Long userId);
}
