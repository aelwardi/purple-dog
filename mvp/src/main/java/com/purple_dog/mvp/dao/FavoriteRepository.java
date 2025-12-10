package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Favorite> findByProductId(Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    long countByUserId(Long userId);

    long countByProductId(Long productId);

    @Query("SELECT f FROM Favorite f LEFT JOIN FETCH f.product p WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    List<Favorite> findByUserIdWithProducts(@Param("userId") Long userId);

    void deleteByUserIdAndProductId(Long userId, Long productId);
}

