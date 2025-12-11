package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.entities.AccountStatus;
import com.purple_dog.mvp.entities.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + person.getRole().name())
        );

        return User.builder()
                .username(person.getEmail())
                .password(person.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(person.getAccountStatus() == AccountStatus.SUSPENDED)
                .credentialsExpired(false)
                .disabled(person.getAccountStatus() != AccountStatus.ACTIVE
                        && person.getAccountStatus() != AccountStatus.PENDING_VERIFICATION)
                .build();
    }

    @Transactional
    public Person loadPersonByEmail(String email) {
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}

