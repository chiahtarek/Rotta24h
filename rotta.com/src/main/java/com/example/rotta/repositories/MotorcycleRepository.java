package com.example.rotta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rotta.models.Motorcycle;


public interface MotorcycleRepository extends JpaRepository<Motorcycle, Integer> {
    
}
