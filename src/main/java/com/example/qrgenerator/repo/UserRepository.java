package com.example.qrgenerator.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.qrgenerator.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
}
