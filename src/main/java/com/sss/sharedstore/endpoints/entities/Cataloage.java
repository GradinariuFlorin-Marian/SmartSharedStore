package com.sss.sharedstore.endpoints.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cataloage")
@Getter
@Setter
public class Cataloage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nume;
    private String linkCatalog;
}
