package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    List<Favorite> findAllByUserId(Long userId);
}
