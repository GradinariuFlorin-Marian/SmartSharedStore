package com.sss.sharedstore.endpoints.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class ControlerAplicatie {

    @GetMapping("/administrare/statusapp")
    public ResponseEntity statusAplicatie() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

