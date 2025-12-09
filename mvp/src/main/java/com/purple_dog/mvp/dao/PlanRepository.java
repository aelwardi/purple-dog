package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Plan;
import com.purple_dog.mvp.entities.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByType(PlanType type);

    List<Plan> findByActiveTrue();

    List<Plan> findByActiveTrueOrderByMonthlyPriceAsc();

    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.features WHERE p.id = :id")
    Optional<Plan> findByIdWithFeatures(@Param("id") Long id);

    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.features WHERE p.type = :type")
    Optional<Plan> findByTypeWithFeatures(@Param("type") PlanType type);

    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.features WHERE p.active = true ORDER BY p.monthlyPrice ASC")
    List<Plan> findAllActiveWithFeatures();

    boolean existsByType(PlanType type);

    @Query("SELECT COUNT(p) FROM Plan p JOIN p.professionals prof WHERE p.id = :planId")
    long countSubscribersByPlanId(@Param("planId") Long planId);
}

