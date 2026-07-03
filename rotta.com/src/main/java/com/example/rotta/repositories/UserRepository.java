package com.example.rotta.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.rotta.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
   public Optional<User> findByLogin(String login); 
}
