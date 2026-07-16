package com.example.rotta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.rotta.models.Mechanic;

public interface MechanicRepository extends JpaRepository<Mechanic, Integer> {
    
}
