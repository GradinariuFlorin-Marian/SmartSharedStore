package com.sss.sharedstore.endpoints.repositories;

import com.sss.sharedstore.endpoints.entities.TokenAdministrare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenAdministrareRepository extends JpaRepository<TokenAdministrare, Long> {
    TokenAdministrare findByToken(String token);
    List<TokenAdministrare> findByNume(String nume);
}