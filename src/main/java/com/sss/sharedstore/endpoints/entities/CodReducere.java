package com.sss.sharedstore.endpoints.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "creducere")
@Getter
@Setter
public class CodReducere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private long idProdus;
    private double sumaRedusa;
    private Date dataExpirare;
    private String cod;
    private boolean folosit;
}
