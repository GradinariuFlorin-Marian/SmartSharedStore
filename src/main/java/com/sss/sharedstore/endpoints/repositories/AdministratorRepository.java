package com.sss.sharedstore.endpoints.repositories;

import com.sss.sharedstore.endpoints.entities.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Administrator findByNume(String nume);
}
