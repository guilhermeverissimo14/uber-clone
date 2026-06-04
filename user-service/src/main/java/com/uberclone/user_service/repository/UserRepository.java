package com.uberclone.user_service.repository;

import com.uberclone.user_service.entity.User;
import com.uberclone.user_service.enums.UserStatus;
import com.uberclone.user_service.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository para operações de banco de dados da entidade User
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        // Busca usuário por email
        Optional<User> findByEmail(String email);

        // Busca usuário por telefone
        Optional<User> findByPhone(String phone);

        // Busca usuário por CPF
        Optional<User> findByCpf(String cpf);

        // Verifica se existe usuário com o email
        boolean existsByEmail(String email);

        // Verifica se existe usuário com o telefone
        boolean existsByPhone(String phone);

        // Verifica se existe usuário com o CPF
        boolean existsByCpf(String cpf);

        // Busca usuários por tipo
        List<User> findByUserType(UserType userType);

        // Busca usuários por status
        List<User> findByStatus(UserStatus status);

        // Busca usuários ativos
        @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
        List<User> findAllActiveUsers();

        // Busca usuários por tipo e status
        List<User> findByUserTypeAndStatus(UserType userType, UserStatus status);

        // Busca usuário por email com todos os relacionamentos (eager)
        @Query("SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.addresses " +
                        "LEFT JOIN FETCH u.passenger " +
                        "LEFT JOIN FETCH u.driver " +
                        "WHERE u.email = :email")
        Optional<User> findByEmailWithRelationships(@Param("email") String email);

        // Busca usuários criados após uma data
        @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
        List<User> findRecentUsers(@Param("date") java.time.LocalDateTime date);

        // Busca top usuários por avaliação
        @Query("SELECT u FROM User u WHERE u.totalRatings > 0 ORDER BY u.ratingAvg DESC")
        List<User> findTopRatedUsers();

        // Conta usuários por tipo
        @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType")
        Long countByUserType(@Param("userType") UserType userType);
}
