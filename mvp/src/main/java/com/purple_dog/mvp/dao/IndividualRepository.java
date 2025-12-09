package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Individual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndividualRepository extends JpaRepository<Individual, Long> {

    List<Individual> findByIdentityVerified(Boolean verified);

    long countByIdentityVerified(Boolean verified);
}

