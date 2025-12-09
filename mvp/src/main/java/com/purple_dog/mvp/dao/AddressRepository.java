package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByPersonIdOrderByIsDefaultDescCreatedAtDesc(Long personId);

    Optional<Address> findByIdAndPersonId(Long id, Long personId);

    Optional<Address> findByPersonIdAndIsDefault(Long personId, Boolean isDefault);

    @Query("SELECT COUNT(a) FROM Address a WHERE a.person.id = :personId")
    long countByPersonId(@Param("personId") Long personId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.person.id = :personId")
    int resetDefaultForPerson(@Param("personId") Long personId);

    boolean existsByIdAndPersonId(Long id, Long personId);
}

