package com.example.rotta.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.rotta.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
   public Optional<User> findByLogin(String login);

   @Modifying(clearAutomatically = true)
   @Query("UPDATE User u SET u.latitude = :lat, u.longitude = :lng, " +
         "u.locationUpdatedAt = :updatedAt, u.online = true " +
         "WHERE u.id = :userId")
   void updateLocation(@Param("userId") Integer userId,
         @Param("lat") Double lat,
         @Param("lng") Double lng,
         @Param("updatedAt") Instant updatedAt);

   @Modifying(clearAutomatically = true)
   @Query("UPDATE User u SET u.online = false WHERE u.id = :userId")
   void markOffline(@Param("userId") Integer userId);
}
