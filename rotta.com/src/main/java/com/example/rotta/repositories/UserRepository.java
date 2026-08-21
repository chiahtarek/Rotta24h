package com.example.rotta.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.rotta.enums.UserRole;
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

      @Modifying(clearAutomatically = true)
      @Query(value = """
                  SELECT * FROM users u
                  WHERE u.online = true
                    AND u.id <> :excludeId
                    AND u.role = :#{#role.name()}
                    AND u.latitude IS NOT NULL AND u.longitude IS NOT NULL
                    AND (6371 * acos(
                          cos(radians(:lat)) * cos(radians(u.latitude)) *
                          cos(radians(u.longitude) - radians(:lng)) +
                          sin(radians(:lat)) * sin(radians(u.latitude))
                        )) <= :radiusKm
                  """, nativeQuery = true)
      List<User> findNearbyOnlineByRole(@Param("lat") Double lat,
                  @Param("lng") Double lng,
                  @Param("radiusKm") Double radiusKm,
                  @Param("excludeId") Integer excludeId,
                  @Param("role") UserRole role);
}
