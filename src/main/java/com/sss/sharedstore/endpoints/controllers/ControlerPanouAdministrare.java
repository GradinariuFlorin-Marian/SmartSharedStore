package com.sss.sharedstore.endpoints.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sss.sharedstore.endpoints.entities.*;
import com.sss.sharedstore.endpoints.repositories.*;
import com.sss.sharedstore.utils.Encriptie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@CrossOrigin(origins = "*")
public class ControlerPanouAdministrare {
    @Autowired
    private AdministratorRepository administratorRepository;
    @Autowired
    private TokenAdministrareRepository tokenAdministrareRepository;
    @Autowired
    private ProdusRepository produsRepository;
    @Autowired
    private StatusAplicatieRepository statusAplicatieRepository;
    @Autowired
    private CosRepository cosRepository;
    @Autowired
    ConturiRepository conturiRepository;
    @Autowired
    CodReducereRepository codReducereRepository;
    @Autowired
    StatisticiRepository statisticiRepository;
    @Autowired
    ProduseProritizateRepository produseProritizateRepository;


    @PostMapping("/administrare/conectare")
    public ResponseEntity conectare(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("nume") != null && nod.get("parola") != null) {
                Administrator administrator = administratorRepository.findByNume(nod.get("nume").textValue());
                if (administrator == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                } else {
                    if (administrator.getParola().equals(Encriptie.returneazaParolaCriptata(nod.get("parola").textValue(), administrator.getCriptare()))) {
                        String token = String.valueOf(UUID.randomUUID());

                        TokenAdministrare tokenAdministrare = new TokenAdministrare();
                        tokenAdministrare.setNume(administrator.getNume());
                        tokenAdministrare.setParola(administrator.getParola());
                        tokenAdministrare.setCreat(new java.sql.Date(System.currentTimeMillis()));
                        tokenAdministrare.setToken(token);

                        tokenAdministrareRepository.save(tokenAdministrare);
                        return new ResponseEntity<>(token, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/adaugacontadmin")
    public ResponseEntity adaugaContAdmin(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode nod = objectMapper.readTree(json);
            TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
            if (token != null) {
                Administrator administrator = administratorRepository.findByNume(token.getNume());
                if (administrator.getParola().equalsIgnoreCase(token.getParola())) {
                    if (administratorRepository.findByNume(nod.get("nume").textValue()) != null) {
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    } else {
                        String salt = Encriptie.genereazaSalt();
                        Administrator administratorNou = new Administrator();
                        administratorNou.setNume(nod.get("parola").textValue());
                        administratorNou.setParola(Encriptie.returneazaParolaCriptata(nod.get("parola").textValue(), salt));
                        administratorNou.setCriptare(salt);

                        administratorRepository.save(administratorNou);

                        return new ResponseEntity<>(HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/stergecontadmin")
    public ResponseEntity stergeContAdmin(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode nod = objectMapper.readTree(json);
            TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
            if (token != null) {
                Administrator ap = administratorRepository.findByNume(token.getNume());
                if (ap.getParola().equalsIgnoreCase(token.getParola())) {
                    Administrator deletedUser = administratorRepository.findByNume(nod.get("nume").textValue());
                    if (deletedUser != null) {
                        tokenAdministrareRepository.findByNume(deletedUser.getNume()).forEach(a -> tokenAdministrareRepository.delete(a));
                        administratorRepository.delete(deletedUser);
                        return new ResponseEntity<>(HttpStatus.OK);
                    } else {
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

    @PostMapping("/administrare/actualizeazaparola")
    public ResponseEntity actualizeazaParola(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null && nod.get("parola") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    Administrator adm = administratorRepository.findByNume(token.getNume());
                    adm.setParola(Encriptie.returneazaParolaCriptata(nod.get("parola").textValue(), adm.getCriptare()));
                    administratorRepository.save(adm);
                    token.setParola(adm.getParola());
                    tokenAdministrareRepository.save(token);
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/returneazaconturi")
    public ResponseEntity returneazaConturiAdmin(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode nod = objectMapper.readTree(json);
            TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
            if (token != null) {
                List<Administrator> adm = administratorRepository.findAll();
                adm.forEach(acc -> acc.setCriptare(""));
                return ResponseEntity.ok().body(adm);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/adaugaprodus")
    public ResponseEntity adaugaProdus(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    if (nod.get("nume") != null && nod.get("distribuitor") != null && nod.get("cantitate") != null && nod.get("descriere") != null && nod.get("pret") != null && nod.get("poza") != null && nod.get("categorie") != null) {

                        Produs produs = new Produs();
                        produs.setNume(nod.get("nume").textValue());
                        produs.setDistribuitor(nod.get("distribuitor").textValue());
                        produs.setCantitate(nod.get("cantitate").textValue());
                        produs.setDescriere(nod.get("descriere").textValue());
                        produs.setPret(nod.get("pret").textValue());
                        produs.setPoza(nod.get("poza").textValue());
                        produs.setCategorie(nod.get("categorie").textValue());
                        produs = produsRepository.save(produs);

                        Statistici statistici = new Statistici();
                        statistici.setNumeProdus(produs.getNume());
                        statistici.setIdProdus(produs.getId());
                        statistici.setDistribuitor(produs.getDistribuitor());
                        statistici.setCategorie(produs.getCategorie());
                        statistici.setAccesari(0);
                        statistici.setCupoaneDeReducereUtilizate(0);
                        statistici.setProduseVandute(0);
                        statisticiRepository.save(statistici);
                        return new ResponseEntity<>(HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
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

    @PostMapping("/administrare/adaugaprodusinrecomandate")
    public ResponseEntity adaugaProdusInRecomandate(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    if (nod.get("id") != null) {

                        ProdusePrioritizate produs = new ProdusePrioritizate();
                        produs.setIdProdus(nod.get("id").intValue());
                        produseProritizateRepository.save(produs);

                        return new ResponseEntity<>(HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
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

    @PostMapping("/administrare/stergeprodusdinrecomandate")
    public ResponseEntity stergeProdusDinRecomandate(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    if (nod.get("id") != null) {

                        ProdusePrioritizate produs = produseProritizateRepository.findByIdProdus(nod.get("id").intValue());
                        if (produs != null) {
                            produseProritizateRepository.delete(produs);
                        } else {
                            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                        }

                        return new ResponseEntity<>(HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
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


    @PostMapping("/administrare/stergeprodus")
    public ResponseEntity stergeProdus(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null && nod.get("token") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    Produs produs = produsRepository.findById(Integer.parseInt(nod.get("idProdus").textValue()));
                    if (produs != null) {
                        statisticiRepository.delete(statisticiRepository.findByIdProdus(produs.getId()));
                        produsRepository.delete(produs);
                        return new ResponseEntity<>(HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/returneazaproduse")
    public ResponseEntity returneazaProduse(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("numeProduse") != null) {
                List<Produs> produse = produsRepository.cautaListaPrinNume(nod.get("numeProduse").textValue());
                return ResponseEntity.ok().body(produse);
            }
            return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/returneazaprodusedinrecomandate")
    public ResponseEntity returneazaProduseDinRecomandate(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("numeProduse") != null) {
                List<ProdusePrioritizate> produse = produseProritizateRepository.cautaListaPrinNume(nod.get("numeProduse").textValue());
                return ResponseEntity.ok().body(produse);
            }
            return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/modificaprodus")
    public ResponseEntity modificaProduse(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {

                    if (nod.get("id") != null && nod.get("valoare") != null && nod.get("valoareNoua") != null) {
                        Produs productEntity = produsRepository.findById(nod.get("id").intValue());
                        var valoareNoua = nod.get("valoareNoua").textValue();
                        switch (nod.get("valoare").textValue()) {
                            case "nume" -> productEntity.setNume(valoareNoua);
                            case "distribuitor" -> productEntity.setDistribuitor(valoareNoua);
                            case "categorie" -> productEntity.setCategorie(valoareNoua);
                            case "cantitate" -> productEntity.setCantitate(valoareNoua);
                            case "descriere" -> productEntity.setDescriere(valoareNoua);
                            case "pret" -> productEntity.setPret(valoareNoua);
                            case "poza" -> productEntity.setPoza(valoareNoua);
                        }

                        produsRepository.save(productEntity);
                        return new ResponseEntity<>(HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/verificaretoken")
    public ResponseEntity verificaTokenAdmin(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode nod = objectMapper.readTree(json);
            TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
            if (token != null) return new ResponseEntity<>(HttpStatus.OK);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/administrare/statusaplicatie")
    public ResponseEntity returneazaStatusAplicatie() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode nod = objectMapper.createObjectNode();
        if (statusAplicatieRepository.findByTip("mentenanta").getValoare().equals("0")) {
            //nod.put("status", 0);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            //nod.put("status", 1);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
//        try {
//            return ResponseEntity.ok(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(nod));
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
    }

    @PostMapping("/administrare/modmentenanta")
    public ResponseEntity modMentenanta(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    StatusAplicatie aps = statusAplicatieRepository.findByTip("mentenanta");
                    if (aps != null) {
                        aps.setValoare(nod.get("mentenanta").textValue());
                        statusAplicatieRepository.save(aps);
                    } else {
                        StatusAplicatie newAps = new StatusAplicatie();
                        newAps.setTip("mentenanta");
                        newAps.setValoare(nod.get("mentenanta").textValue());
                        statusAplicatieRepository.save(newAps);
                    }
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

    @PostMapping("/administrare/stergecomenzi")
    public ResponseEntity stergeComenzi(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());

                if (token != null) {
                    cosRepository.deleteAll();
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/returneazacomenzi")
    public ResponseEntity returneazaComenzi(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    return ResponseEntity.ok().body(cosRepository.findAll());
                }
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/actualizeazastatuscomanda")
    public ResponseEntity actualizeazaStatusComanda(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null && nod.get("idComanda") != null && nod.get("statusComanda") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    Cos cos = cosRepository.findById(nod.get("idComanda").intValue());
                    if (cos != null) {
                        cos.setStatusComanda(nod.get("statusComanda").textValue());
                        cosRepository.save(cos);
                        return new ResponseEntity<>(HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/returneazacomenzifiltrate")
    public ResponseEntity returneazaComenziFiltrate(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null && nod.get("status") != null && nod.get("filtru") != null && nod.get("valoareFiltru") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    String status = nod.get("status").textValue();
                    String filtru = nod.get("filtru").textValue();
                    String valoareFiltru = nod.get("valoareFiltru").textValue();
                    if (status.equals("Fara valoare") && filtru.equals("Fara valoare")) {
                        return ResponseEntity.ok().body(cosRepository.findAll());
                    } else {
                        if (status.equals("Fara valoare")) {
                            return switch (filtru) {
                                case "Email" ->
                                        ResponseEntity.ok().body(cosRepository.cautaListaPrinEmail(valoareFiltru));
                                case "Numar telefon" ->
                                        ResponseEntity.ok().body(cosRepository.cautaListaPrinNumarTelefon(valoareFiltru));
                                case "Adresa" ->
                                        ResponseEntity.ok().body(cosRepository.cautaListaPrinAdresa(valoareFiltru));
                                default -> ResponseEntity.ok().body(Collections.emptyMap());
                            };
                        } else if (filtru.equals("Fara valoare")) {
                            return ResponseEntity.ok().body(cosRepository.cautaListaPrinStatus(status));
                        } else {
                            return switch (filtru) {
                                case "Email" ->
                                        ResponseEntity.ok().body(cosRepository.cautaListaPrinStatusSiEmail(status, valoareFiltru));
                                case "Numar telefon" ->
                                        ResponseEntity.ok().body(cosRepository.cautaListaPrinStatusSiNumarTelefon(status, valoareFiltru));
                                case "Adresa" ->
                                        ResponseEntity.ok().body(cosRepository.cautaListaPrinStatusSiAdresa(status, valoareFiltru));
                                default -> ResponseEntity.ok().body(Collections.emptyMap());
                            };
                        }
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/adaugacodreducereglobal")
    public ResponseEntity adaugaCodReducereGlobal(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode values = objectMapper.readTree(json);
            if (values.get("token") != null && values.get("idProdus") != null && values.get("sumaRedusa") != null && values.get("cod") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(values.get("token").textValue());
                if (token != null) {
                    for (Conturi cont : conturiRepository.findAll()) {
                        CodReducere codReducere = new CodReducere();
                        codReducere.setEmail(cont.getEmail());
                        codReducere.setIdProdus(Integer.parseInt(values.get("idProdus").textValue()));
                        codReducere.setSumaRedusa(Double.parseDouble(values.get("sumaRedusa").textValue()));
                        codReducere.setCod(values.get("cod").textValue());
                        //LocalDate date = LocalDate.of(LocalDate.now().getYear(), Integer.parseInt(values.get("luna").textValue(), Integer.parseInt(values.ge)))
                        codReducere.setFolosit(false);

                        codReducereRepository.save(codReducere);
                    }
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/stergecodreducereglobal")
    public ResponseEntity stergeCodReducereGlobal(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null && nod.get("cod") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    for (CodReducere codReducere : codReducereRepository.findAll().stream().filter(reducere -> reducere.getCod().equals(nod.get("cod").textValue())).toList()) {
                        codReducereRepository.delete(codReducere);
                    }
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/adaugacodreducerepersonal")
    public ResponseEntity adaugaCodReducerePersonal(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null && nod.get("email") != null && nod.get("idProdus") != null && nod.get("sumaRedusa") != null && nod.get("cod") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    Conturi cont = conturiRepository.findByEmail(nod.get("email").textValue());
                    if (cont != null) {
                        CodReducere codReducere = new CodReducere();
                        codReducere.setEmail(cont.getEmail());
                        codReducere.setIdProdus(Integer.parseInt(nod.get("idProdus").textValue()));
                        codReducere.setSumaRedusa(Double.parseDouble(nod.get("sumaRedusa").textValue()));
                        codReducere.setCod(nod.get("cod").textValue());
                        codReducere.setFolosit(false);
                        codReducereRepository.save(codReducere);
                    } else {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/returneazastatistici")
    public ResponseEntity statisticiGlobale(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    return ResponseEntity.ok().body(statisticiRepository.findAll());
                }
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/statisticiaplicatie")
    public ResponseEntity statisticiAplicatie(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    Map<String, String> body = new HashMap<>();
                    body.put("conturi", String.valueOf(conturiRepository.findAll().size()));
                    body.put("produse", String.valueOf(produsRepository.findAll().size()));

                    AtomicLong valoare = new AtomicLong();
                    cosRepository.findAll().forEach(produs ->
                            produs.getProduseInCos().forEach(prod -> valoare.addAndGet(Long.parseLong(prod.split(":")[1]))));
                    body.put("produseVandute", String.valueOf(valoare.get()));

                    return ResponseEntity.ok().body(body);
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/administrare/returneazacomanda")
    public ResponseEntity returneazaComandaA(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("token") != null && nod.get("idComanda") != null) {
                TokenAdministrare token = tokenAdministrareRepository.findByToken(nod.get("token").textValue());
                if (token != null) {
                    Cos cos = cosRepository.findById(Long.parseLong(nod.get("idComanda").textValue()));
                    if (cos != null) {
                        return ResponseEntity.ok().body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cos));
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/administrare/returneazacomandacuproduse/{idComanda}")
    public ResponseEntity returneazaComandaCuProduse(@RequestHeader("token") String tokenCont, @PathVariable long idComanda) {
        try {
            TokenAdministrare token = tokenAdministrareRepository.findByToken(tokenCont);
            if (token != null) {
                Cos cos = cosRepository.findById(idComanda);
                if (cos != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<String> produse = new ArrayList<>();
                    for (String produs : cos.getProduseInCos()) {
                        String[] split = produs.split(":");
                        Produs prod = produsRepository.findById(Integer.parseInt(split[0]));
                        prod.setCantitate(split[1]);
                        produse.add(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(prod));
                    }
                    cos.setProduseInCos(produse);
                    return ResponseEntity.ok().body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cos));
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
