package org.devquality.safetyauthservice.persistence.repositories;

import org.devquality.safetyauthservice.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Long> {

}
