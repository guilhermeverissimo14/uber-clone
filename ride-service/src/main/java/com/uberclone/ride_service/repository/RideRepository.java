package com.uberclone.ride_service.repository;

import com.uberclone.ride_service.entity.Ride;
import com.uberclone.ride_service.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    // Busca todas as corridas de um passageiro
    List<Ride> findByPassengerIdOrderByCreatedAtDesc(Long passengerId);

    // Busca todas as corridas de um motorista
    List<Ride> findByDriverIdOrderByCreatedAtDesc(Long driverId);

    // Busca corridas por status
    List<Ride> findByStatus(RideStatus status);

    // Busca corridas de um passageiro por status
    List<Ride> findByPassengerIdAndStatus(Long passengerId, RideStatus status);

    // Busca corridas de um motorista por status
    List<Ride> findByDriverIdAndStatus(Long driverId, RideStatus status);

    // Busca a corrida ativa de um passageiro (só pode ter uma por vez)
    @Query("SELECT r FROM Ride r WHERE r.passengerId = :passengerId " +
           "AND r.status IN ('REQUESTED', 'MATCHED', 'ACCEPTED', 'IN_PROGRESS')")
    Optional<Ride> findActiveRideByPassengerId(@Param("passengerId") Long passengerId);

    // Busca a corrida ativa de um motorista
    @Query("SELECT r FROM Ride r WHERE r.driverId = :driverId " +
           "AND r.status IN ('ACCEPTED', 'IN_PROGRESS')")
    Optional<Ride> findActiveRideByDriverId(@Param("driverId") Long driverId);

    // Verifica se o passageiro já tem uma corrida ativa
    @Query("SELECT COUNT(r) > 0 FROM Ride r WHERE r.passengerId = :passengerId " +
           "AND r.status IN ('REQUESTED', 'MATCHED', 'ACCEPTED', 'IN_PROGRESS')")
    boolean hasActiveRide(@Param("passengerId") Long passengerId);

    // Conta corridas por status (útil para dashboards)
    Long countByStatus(RideStatus status);
}

