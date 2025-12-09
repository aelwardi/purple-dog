package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    List<Admin> findBySuperAdmin(Boolean superAdmin);

    long countBySuperAdmin(Boolean superAdmin);
}
