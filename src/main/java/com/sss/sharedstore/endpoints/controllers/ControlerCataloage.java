package com.sss.sharedstore.endpoints.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sss.sharedstore.endpoints.entities.Cataloage;
import com.sss.sharedstore.endpoints.entities.Produs;
import com.sss.sharedstore.endpoints.entities.ProdusePrioritizate;
import com.sss.sharedstore.endpoints.entities.TokenAdministrare;
import com.sss.sharedstore.endpoints.repositories.CataloageRepository;
import com.sss.sharedstore.endpoints.repositories.TokenAdministrareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class ControlerCataloage {
    @Autowired
    CataloageRepository cataloageRepository;
    @Autowired
    private TokenAdministrareRepository tokenAdministrareRepository;

    @GetMapping("/cataloage/returneazacataloage")
    public ResponseEntity returneazaProduse() {
                return ResponseEntity.ok().body(cataloageRepository.findAll());
    }

    @PostMapping("/cataloage/adaugacatalog")
    public ResponseEntity adaugaCatalog(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null && nod.get("numeCatalog") != null && nod.get("linkCatalog") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {

                        Cataloage catalog = new Cataloage();
                        catalog.setNume(nod.get("numeCatalog").textValue());
                        catalog.setLinkCatalog(nod.get("linkCatalog").textValue());
                        cataloageRepository.save(catalog);
                        return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cataloage/stergecatalog")
    public ResponseEntity stergeCatalog(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null && nod.get("idCatalog") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {

                    Cataloage catalog = cataloageRepository.findById(Long.parseLong(nod.get("idCatalog").textValue()));
                    if(catalog !=null) {
                        cataloageRepository.delete(catalog);
                        return new ResponseEntity<>(HttpStatus.OK);
                    }else{
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
