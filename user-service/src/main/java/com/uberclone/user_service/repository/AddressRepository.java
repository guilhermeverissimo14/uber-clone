package com.uberclone.user_service.repository;

import com.uberclone.user_service.entity.Address;
import com.uberclone.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository para operações de banco de dados da entidade Address
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // Busca endereços por ID do usuário
    List<Address> findByUserId(Long userId);

    // Busca endereços pelo objeto User
    List<Address> findByUser(User user);

    // Busca endereço padrão do usuário
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isDefault = true")
    Optional<Address> findDefaultAddressByUserId(@Param("userId") Long userId);

    // Busca endereços por label (HOME, WORK, etc)
    List<Address> findByUserIdAndLabel(Long userId, String label);

    // Busca endereços por cidade
    List<Address> findByCity(String city);

    // Busca endereços por estado
    List<Address> findByState(String state);

    // Busca endereços por CEP
    List<Address> findByZipCode(String zipCode);

    // Busca endereços de um usuário por cidade
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.city = :city")
    List<Address> findByUserIdAndCity(@Param("userId") Long userId, @Param("city") String city);

    // Conta endereços do usuário
    Long countByUserId(Long userId);

    // Deleta endereços por ID do usuário
    void deleteByUserId(Long userId);

    // Remove flag de default de todos os endereços do usuário
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void unsetDefaultAddressByUserId(@Param("userId") Long userId);
}
