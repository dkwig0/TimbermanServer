package com.example.timbermanserver.repositories;


import com.example.timbermanserver.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    User findUserByActivationCode(String activationCode);

}
