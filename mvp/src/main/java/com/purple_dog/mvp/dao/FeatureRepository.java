package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    Optional<Feature> findByCode(String code);

    List<Feature> findByActiveTrue();

    List<Feature> findByActiveTrueOrderByNameAsc();

    boolean existsByCode(String code);

    boolean existsByName(String name);

    @Query("SELECT f FROM Feature f JOIN f.plans p WHERE p.id = :planId")
    List<Feature> findByPlanId(@Param("planId") Long planId);

    @Query("SELECT COUNT(f) FROM Feature f JOIN f.plans p WHERE f.id = :featureId")
    long countPlansByFeatureId(@Param("featureId") Long featureId);
}

