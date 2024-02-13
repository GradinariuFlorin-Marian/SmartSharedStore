package com.sss.sharedstore.endpoints.entities;

import com.sss.sharedstore.endpoints.TipPlati;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "cos")
@Getter
@Setter
public class Cos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private List<String> produseInCos;
    private String statusComanda;
    private String numarTelefon;
    private String email;
    private String idComanda;
    private String adresa;
    private String pretTotal;
    private String dataComenzi;
    private TipPlati platitPrin;
}