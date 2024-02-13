package com.sss.sharedstore.endpoints;

public enum TipPlati {
    APPLEPAY("APPLEPAY"),
    STRIPE("STRIPE"),
    CASH("CASH");

    public final String eticheta;

    TipPlati(String eticheta) {
        this.eticheta = eticheta;
    }
}
