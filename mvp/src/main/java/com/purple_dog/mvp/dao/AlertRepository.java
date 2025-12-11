package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Alert> findByUserIdAndActiveTrue(Long userId);

    List<Alert> findByActiveTrue();

    @Query("SELECT a FROM Alert a WHERE a.user.id = :userId AND a.category.id = :categoryId")
    List<Alert> findByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    @Query("SELECT a FROM Alert a LEFT JOIN FETCH a.category WHERE a.id = :id")
    Optional<Alert> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT COUNT(a) FROM Alert a WHERE a.user.id = :userId AND a.active = true")
    long countActiveByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndCategoryIdAndActiveTrue(Long userId, Long categoryId);
}

