package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByPersonId(Long personId);
    
    Optional<Address> findByPersonIdAndIsDefaultTrue(Long personId);
    
    List<Address> findByCity(String city);
    
    List<Address> findByCountry(String country);
    
    @Query("SELECT a FROM Address a WHERE a.person.id = :personId AND a.label = :label")
    Optional<Address> findByPersonIdAndLabel(@Param("personId") Long personId, 
                                             @Param("label") String label);
    
    long countByPersonId(Long personId);
    
    boolean existsByPersonIdAndIsDefaultTrue(Long personId);
}
