package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    List<Admin> findBySuperAdmin(Boolean superAdmin);

    long countBySuperAdmin(Boolean superAdmin);
}
