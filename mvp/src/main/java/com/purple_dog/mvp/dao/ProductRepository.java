package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Product;
import com.purple_dog.mvp.entities.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findById(Long id);

    List<Product> findAllBySellerId(Long sellerId);

    List<Product> findAllBySellerIdAndStatus(Long sellerId, ProductStatus status);
}
