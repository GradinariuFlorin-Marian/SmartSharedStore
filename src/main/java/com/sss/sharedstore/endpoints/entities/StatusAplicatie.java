package com.sss.sharedstore.endpoints.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="status")
@Getter
@Setter
public class StatusAplicatie {

    @Id
    @GeneratedValue
    private long id;
    private String tip;
    private String valoare;
}
