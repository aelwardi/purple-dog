package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.StripeCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StripeCustomerRepository extends JpaRepository<StripeCustomer, Long> {

    Optional<StripeCustomer> findByStripeCustomerId(String stripeCustomerId);

    @Query("SELECT sc FROM StripeCustomer sc WHERE sc.user.id = :userId")
    Optional<StripeCustomer> findByUserId(@Param("userId") Long userId);

    Optional<StripeCustomer> findByEmail(String email);

    boolean existsByStripeCustomerId(String stripeCustomerId);

    @Query("SELECT CASE WHEN COUNT(sc) > 0 THEN true ELSE false END FROM StripeCustomer sc WHERE sc.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}

