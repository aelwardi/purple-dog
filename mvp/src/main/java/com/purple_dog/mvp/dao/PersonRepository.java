package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.Person;
import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmail(String email);

    Optional<Person> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<Person> findByRole(UserRole role);

    List<Person> findByAccountStatus(AccountStatus status);

    List<Person> findByRoleAndAccountStatus(UserRole role, AccountStatus status);

    @Query("SELECT p FROM Person p WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Person> searchByKeyword(@Param("keyword") String keyword);

    long countByRole(UserRole role);

    long countByAccountStatus(AccountStatus status);

    long countByCreatedAtAfter(java.time.LocalDateTime dateTime);

    long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
