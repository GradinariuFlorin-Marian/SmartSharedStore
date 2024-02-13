package com.sss.sharedstore.endpoints.repositories;

import com.sss.sharedstore.endpoints.entities.Conturi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConturiRepository extends JpaRepository<Conturi, Long> {
    Conturi findByEmail(String email);
}