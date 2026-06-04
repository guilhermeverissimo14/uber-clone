package com.uberclone.user_service.repository;


import com.uberclone.user_service.entity.Passenger;
import com.uberclone.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository para operações de banco de dados da entidade Passenger
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    // Busca passageiro por ID do usuário
    Optional<Passenger> findByUserId(Long userId);

    // Busca passageiro pelo objeto User
    Optional<Passenger> findByUser(User user);

    // Verifica se existe passageiro para o usuário
    boolean existsByUserId(Long userId);

    // Busca passageiro com dados do usuário (eager)
    @Query("SELECT p FROM Passenger p " +
            "JOIN FETCH p.user " +
            "WHERE p.id = :id")
    Optional<Passenger> findByIdWithUser(@Param("id") Long id);

    // Busca passageiro por email do usuário
    @Query("SELECT p FROM Passenger p " +
            "JOIN p.user u " +
            "WHERE u.email = :email")
    Optional<Passenger> findByUserEmail(@Param("email") String email);

    // Deleta passageiro por ID do usuário
    void deleteByUserId(Long userId);
}
