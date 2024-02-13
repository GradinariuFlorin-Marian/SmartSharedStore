package com.sss.sharedstore.endpoints.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.sql.Date;

@Entity
@Table(name = "tokeni")
@Getter
@Setter
public class TokenAdministrare {
    @Id
    @GeneratedValue
    private long id;
    @NotNull
    private String nume;
    @NotNull
    private String parola;
    @NotNull
    private Date creat;
    @NotNull
    private String token;

    public String toString() {
        return "Id: " + this.id + ", Nume:" + this.nume + ", Parola: " + this.parola + ", Token: " + this.token +
                ", Creat: " + creat.toString();
    }
}
