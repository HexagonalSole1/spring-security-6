package org.devquality.safetyauthservice.persistence.repositories;

import org.devquality.safetyauthservice.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.fcmToken = :fcmToken")
    Optional<User> findByFcmToken(@Param("fcmToken") String fcmToken);

    @Query("SELECT u FROM User u WHERE u.active = true")
    java.util.List<User> findAllActiveUsers();

    @Query("SELECT u FROM User u WHERE u.role = :role")
    java.util.List<User> findByRole(@Param("role") org.devquality.safetyauthservice.persistence.enums.UserRole role);

    @Query("SELECT u FROM User u WHERE u.verified = true")
    java.util.List<User> findAllVerifiedUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    Long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") org.devquality.safetyauthservice.persistence.enums.UserRole role);
}