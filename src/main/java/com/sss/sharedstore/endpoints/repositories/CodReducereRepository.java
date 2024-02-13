package com.sss.sharedstore.endpoints.repositories;

import com.sss.sharedstore.endpoints.entities.CodReducere;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodReducereRepository extends JpaRepository<CodReducere, Long> {
    List<CodReducere> findByEmail(String email);
}
