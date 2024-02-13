package com.sss.sharedstore.endpoints.repositories;

import com.sss.sharedstore.endpoints.entities.StatusAplicatie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusAplicatieRepository extends JpaRepository<StatusAplicatie, Long> {
    StatusAplicatie findByTip(String tip);
}