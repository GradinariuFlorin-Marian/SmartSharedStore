package com.sss.sharedstore.endpoints.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "produse")
@Getter
@Setter
public class Produs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private String nume;
    @NonNull
    private String distribuitor;
    @NonNull
    private String cantitate;
    @NonNull
    private String descriere;
    @NonNull
    private String pret;
    @NonNull
    private String categorie;
    @NonNull
    private String poza;
}