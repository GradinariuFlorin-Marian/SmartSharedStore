package com.sss.sharedstore.endpoints.repositories;

import com.sss.sharedstore.endpoints.entities.Cataloage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CataloageRepository  extends JpaRepository<Cataloage, Long> {

    Cataloage findById(long id);

    Cataloage findByNume(String nume);

}
