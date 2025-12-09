package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    Optional<Professional> findBySiret(String siret);

    Optional<Professional> findByTvaNumber(String tvaNumber);

    boolean existsBySiret(String siret);

    boolean existsByTvaNumber(String tvaNumber);

    List<Professional> findByCertified(Boolean certified);

    List<Professional> findBySpecialty(String specialty);

    @Query("SELECT p FROM Professional p WHERE LOWER(p.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Professional> searchByCompanyName(@Param("keyword") String keyword);

    long countByCertified(Boolean certified);
}

