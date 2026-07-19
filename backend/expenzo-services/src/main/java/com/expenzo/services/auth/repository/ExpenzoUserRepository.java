package com.expenzo.services.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expenzo.services.auth.repository.model.ExpenzoUser;

@Repository
public interface ExpenzoUserRepository extends JpaRepository<ExpenzoUser, String> {

    Optional<ExpenzoUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
