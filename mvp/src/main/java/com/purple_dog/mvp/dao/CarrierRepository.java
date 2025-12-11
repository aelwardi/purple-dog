package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {

    Optional<Carrier> findByCode(String code);

    Optional<Carrier> findByName(String name);

    List<Carrier> findByActiveTrue();

    List<Carrier> findByActiveTrueOrderByNameAsc();

    boolean existsByCode(String code);

    boolean existsByName(String name);

    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.carrier.id = :carrierId")
    long countDeliveriesByCarrierId(@Param("carrierId") Long carrierId);

    @Query("SELECT c FROM Carrier c WHERE c.active = true AND c.basePrice <= :maxPrice ORDER BY c.basePrice ASC")
    List<Carrier> findAvailableCarriersByMaxPrice(@Param("maxPrice") java.math.BigDecimal maxPrice);
}

