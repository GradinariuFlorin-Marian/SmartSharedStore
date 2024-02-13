package com.sss.sharedstore.endpoints.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "administrator")
@Getter
@Setter
public class Administrator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nume", nullable = false, unique = true)
    private String nume;

    @NotNull
    private String parola;
    @NotNull
    private String criptare;

    public String toString() {
        return "Id: " + this.id + ", Nume:" + this.nume;
    }
}