package com.sss.sharedstore.endpoints.repositories;

import com.sss.sharedstore.endpoints.entities.Statistici;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatisticiRepository extends JpaRepository<Statistici, Long> {

    Statistici findByIdProdus(long idProdus);

    @Query(value = "SELECT * FROM statistici WHERE nume LIKE %?1%", nativeQuery = true)
    List<Statistici> cautaListaPrinNume(String name);

    @Query(value = "SELECT * FROM statistici WHERE categorie LIKE %?1%", nativeQuery = true)
    List<Statistici> cautaListaPrinCategorie(String categorie);

    @Query(value = "SELECT * FROM statistici WHERE distribuitor LIKE %?1%", nativeQuery = true)
    List<Statistici> cautaListaPrinDistribuitor(String distribuitor);

}