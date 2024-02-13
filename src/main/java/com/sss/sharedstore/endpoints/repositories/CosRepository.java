package com.sss.sharedstore.endpoints.repositories;

import com.sss.sharedstore.endpoints.entities.Cos;
import com.sss.sharedstore.endpoints.entities.Produs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CosRepository extends JpaRepository<Cos, Long> {
    Cos findById(long id);

    @Query(value = "SELECT * FROM cos WHERE status_comanda LIKE %?1%", nativeQuery = true)
    List<Cos> cautaListaPrinStatus(String status);
    @Query(value = "SELECT * FROM cos WHERE email LIKE %?1%", nativeQuery = true)
    List<Cos> cautaListaPrinEmail(String email);
    @Query(value = "SELECT * FROM cos WHERE numar_telefon LIKE %?1%", nativeQuery = true)
    List<Cos> cautaListaPrinNumarTelefon(String numarTelefon);
    @Query(value = "SELECT * FROM cos WHERE adresa LIKE %?1%", nativeQuery = true)
    List<Cos> cautaListaPrinAdresa(String adresa);

    @Query(value = "SELECT * FROM cos WHERE status_comanda LIKE %?1% AND email LIKE %?2%", nativeQuery = true)
    List<Cos> cautaListaPrinStatusSiEmail(String statusComanda, String email);
    @Query(value = "SELECT * FROM cos WHERE status_comanda LIKE %?1% AND numar_telefon LIKE %?2%", nativeQuery = true)
    List<Cos> cautaListaPrinStatusSiNumarTelefon(String statusComanda, String numarTelefon);
    @Query(value = "SELECT * FROM cos WHERE status_comanda LIKE %?1% AND adresa LIKE %?2%", nativeQuery = true)
    List<Cos> cautaListaPrinStatusSiAdresa(String statusComanda, String adresa);
}
