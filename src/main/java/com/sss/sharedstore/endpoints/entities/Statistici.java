package com.sss.sharedstore.endpoints.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "statistici")
@Getter
@Setter
public class Statistici {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long idProdus;
    private String numeProdus;
    private String categorie;
    private String distribuitor;
    private long produseVandute;
    private long cupoaneDeReducereUtilizate;
    private long accesari;
}
