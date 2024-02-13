package com.sss.sharedstore.endpoints.repositories;

import com.sss.sharedstore.endpoints.entities.Produs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdusRepository extends JpaRepository<Produs, Long> {

    Produs findById(int id);

    Produs findByNume(String nume);

    Produs findByDistribuitor(String distribuitor);

    @Query(value = "SELECT * FROM produse WHERE id LIKE %?1%", nativeQuery = true)
    List<Produs> cautaListaPrinId(String id);

    @Query(value = "SELECT * FROM produse WHERE nume LIKE %?1%", nativeQuery = true)
    List<Produs> cautaListaPrinNume(String name);

    @Query(value = "SELECT * FROM produse WHERE distribuitor LIKE %?1%", nativeQuery = true)
    List<Produs> cautaListaPrinDistribuitor(String distribuitor);

    @Query(value = "SELECT * FROM produse WHERE CONCAT(pret, '') LIKE %?1%", nativeQuery = true)
    List<Produs> cautaListaPrinPret(String pret);

    @Query(value = "SELECT * FROM produse WHERE categorie LIKE %?1%", nativeQuery = true)
    List<Produs> cautaListaPrinCategorie(String categorie);
}
