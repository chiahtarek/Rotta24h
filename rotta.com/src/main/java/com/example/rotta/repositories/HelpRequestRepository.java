package com.example.rotta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.rotta.enums.HelpRequestStatus;
import com.example.rotta.models.HelpRequest;
import com.example.rotta.models.User;

@Repository
public interface HelpRequestRepository extends JpaRepository<HelpRequest, Integer> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE HelpRequest h SET h.helper = :helper, h.status = :status WHERE h.id = :requestId AND h.helper IS NULL")
    int acceptIfAvailable(@Param("requestId") Integer requestId, @Param("helper") User helper, @Param("status")HelpRequestStatus status);

}
