package com.sss.sharedstore.endpoints.repositories;

import com.sss.sharedstore.endpoints.entities.ProdusePrioritizate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProduseProritizateRepository extends JpaRepository<ProdusePrioritizate, Long> {
    ProdusePrioritizate findByIdProdus(long idProdus);

    @Query(value = "SELECT * FROM prodp WHERE nume LIKE %?1%", nativeQuery = true)
    List<ProdusePrioritizate> cautaListaPrinNume(String name);
}
