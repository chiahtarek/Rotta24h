package com.example.rotta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rotta.models.HelpRequest;

@Repository
public interface HelpRequestRepository extends JpaRepository<HelpRequest, Integer> {
    
}
