package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Platform;
import com.purple_dog.mvp.entities.PlatformStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    Optional<Platform> findByName(String name);

    Optional<Platform> findByStatus(PlatformStatus status);

    @Query("SELECT p FROM Platform p LEFT JOIN FETCH p.reviews WHERE p.id = :id")
    Optional<Platform> findByIdWithReviews(Long id);

    /**
     * Récupérer l'instance unique de la plateforme
     * La plateforme est un singleton
     */
    @Query("SELECT p FROM Platform p ORDER BY p.id ASC LIMIT 1")
    Optional<Platform> findPlatformInstance();
}

