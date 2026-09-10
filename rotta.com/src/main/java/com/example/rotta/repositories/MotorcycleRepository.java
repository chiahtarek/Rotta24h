package com.example.rotta.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.rotta.models.Motorcycle;


public interface MotorcycleRepository extends JpaRepository<Motorcycle, Integer> {
    @Modifying(clearAutomatically = true)
    @Query ("SELECT m from Motorcycle m WHERE m.rider.id = :userId ")
    List<Motorcycle> listById(@Param ("userId")Integer userId); 

}
