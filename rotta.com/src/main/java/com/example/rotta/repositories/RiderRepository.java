package com.example.rotta.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.rotta.models.Rider;
import com.example.rotta.models.User;

public interface RiderRepository extends JpaRepository<Rider, Integer> {
    Optional<Rider> findByUser(User user);
}
