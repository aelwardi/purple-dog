package com.purple_dog.mvp.dao;

import com.purple_dog.mvp.entities.PasswordResetToken;
import com.purple_dog.mvp.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByPerson(Person person);

    void deleteByExpiryDateBefore(LocalDateTime now);
}

