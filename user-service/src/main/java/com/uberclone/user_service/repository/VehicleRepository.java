package com.uberclone.user_service.repository;

import com.uberclone.user_service.entity.Driver;
import com.uberclone.user_service.entity.Vehicle;
import com.uberclone.user_service.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository para operações de banco de dados da entidade Vehicle
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Busca veículos por ID do motorista
    List<Vehicle> findByDriverId(Long driverId);

    // Busca veículos pelo objeto Driver
    List<Vehicle> findByDriver(Driver driver);

    // Busca veículo por placa
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    // Verifica se existe veículo com a placa
    boolean existsByLicensePlate(String licensePlate);

    // Busca veículos ativos do motorista
    @Query("SELECT v FROM Vehicle v WHERE v.driver.id = :driverId AND v.isActive = true")
    List<Vehicle> findActiveVehiclesByDriverId(@Param("driverId") Long driverId);

    // Busca veículos por tipo
    List<Vehicle> findByVehicleType(VehicleType vehicleType);

    // Busca veículos ativos por tipo
    @Query("SELECT v FROM Vehicle v WHERE v.vehicleType = :vehicleType AND v.isActive = true")
    List<Vehicle> findActiveVehiclesByType(@Param("vehicleType") VehicleType vehicleType);

    // Busca o veículo ativo principal do motorista
    @Query("SELECT v FROM Vehicle v " +
            "WHERE v.driver.id = :driverId " +
            "AND v.isActive = true " +
            "ORDER BY v.createdAt DESC")
    Optional<Vehicle> findPrimaryVehicleByDriverId(@Param("driverId") Long driverId);

    // Busca veículos por marca
    List<Vehicle> findByBrand(String brand);

    // Busca veículos por modelo
    List<Vehicle> findByModel(String model);

    // Busca veículos por ano
    List<Vehicle> findByYear(Integer year);

    // Conta veículos ativos do motorista
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.driver.id = :driverId AND v.isActive = true")
    Long countActiveVehiclesByDriverId(@Param("driverId") Long driverId);

    // Deleta veículos por ID do motorista
    void deleteByDriverId(Long driverId);
}
