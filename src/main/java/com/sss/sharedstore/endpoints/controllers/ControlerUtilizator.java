package com.sss.sharedstore.endpoints.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sss.sharedstore.endpoints.TipPlati;
import com.sss.sharedstore.endpoints.entities.*;
import com.sss.sharedstore.endpoints.repositories.*;
import com.sss.sharedstore.utils.Utilitati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class ControlerUtilizator {
    @Autowired
    CosRepository cosRepository;
    @Autowired
    ProdusRepository produsRepository;
    @Autowired
    ConturiRepository conturiRepository;

    @Autowired
    CodReducereRepository codReducereRepository;
    @Autowired
    StatisticiRepository statisticiRepository;
    @Autowired
    ProduseProritizateRepository produseProritizateRepository;

    @GetMapping("/utilizator/primesteproduse")
    public ResponseEntity primesteProduse() {
        return ResponseEntity.ok().body(produsRepository.findAll());
    }

    @PostMapping("/utilizator/returneazaproduseprinfiltru")
    public ResponseEntity returneazaProdusePrinFiltru(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            String mesaj = nod.get("mesajCautare").textValue();
            List<Produs> produse = switch (nod.get("filtru").textValue()) {
                case "id" -> produsRepository.cautaListaPrinId(mesaj);
                case "nume" -> produsRepository.cautaListaPrinNume(mesaj);
                case "distribuitor" -> produsRepository.cautaListaPrinDistribuitor(mesaj);
                case "pret" -> produsRepository.cautaListaPrinPret(mesaj);
                case "categorie" -> produsRepository.cautaListaPrinCategorie(mesaj);
                default -> new ArrayList<>();
            };

            return ResponseEntity.ok().body(produse);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/utilizator/primesteproduse/{idProdus}")
    public ResponseEntity primesteProduse(@PathVariable int idProdus) throws JsonProcessingException {
        Produs produs = produsRepository.findById(idProdus);
        if (produs != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode body = objectMapper.createObjectNode();
            body.put("idProdus", String.valueOf(produs.getId()));
            body.put("nume", produs.getNume());
            body.put("cantitate", produs.getCantitate());
            body.put("descriere", produs.getDescriere());
            body.put("pret", produs.getPret());
            body.put("poza", produs.getPoza());
            body.put("distribuitor", produs.getDistribuitor());
            body.put("categorie", produs.getCategorie());
            return ResponseEntity.ok(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/utilizator/comandaproduse")
    public ResponseEntity comandaProduse(@RequestBody String json) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("adresa") != null && nod.get("numarTelefon") != null && nod.get("email") != null && nod.get("pret") != null
                    && nod.get("tipPlata") != null && nod.get("cos") != null) {

                Cos cos = new Cos();

                cos.setStatusComanda("In asteptare");
                cos.setAdresa(nod.get("adresa").textValue());
                cos.setNumarTelefon(nod.get("numarTelefon").textValue());
                cos.setEmail(nod.get("email").textValue());
                cos.setPretTotal(nod.get("pret").textValue());
                LocalDate currentdate = LocalDate.now();
                cos.setDataComenzi(currentdate.getDayOfMonth() + "/" + currentdate.getMonthValue() + "/" + currentdate.getYear());
                switch (nod.get("tipPlata").textValue()) {
                    case "STRIPE":
                        cos.setPlatitPrin(TipPlati.STRIPE);
                        break;
                    case "APPLEPAY":
                        cos.setPlatitPrin(TipPlati.APPLEPAY);
                        break;
                    default:
                        cos.setPlatitPrin(TipPlati.CASH);
                        break;
                }

                String[] str = nod.get("cos").asText().split(" ");
                for (String produs : str) {
                    Statistici statistici = statisticiRepository.findByIdProdus(Long.parseLong(produs.split(":")[0]));
                    statistici.setAccesari(statistici.getAccesari() + Long.parseLong(produs.split(":")[1]));
                    statisticiRepository.save(statistici);
                }
                cos.setProduseInCos(List.of(str));

                cosRepository.save(cos);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/utilizator/returneazaComanda")
    public ResponseEntity returneazaComanda(@RequestBody String json) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("idComanda") != null && nod.get("email") != null) {
                String idComanda = nod.get("idComanda").textValue();
                String email = nod.get("email").textValue();
                Cos comanda = cosRepository.findById(Integer.parseInt(idComanda));
                if (comanda != null) {
                    if (Objects.equals(comanda.getEmail(), email)) {
                        return ResponseEntity.ok().body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(comanda));
                    } else {
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/utilizator/adaugacont")
    public ResponseEntity adaugaCont(@RequestBody String json) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("email") != null) {
                String email = nod.get("email").textValue();
                Conturi cont = conturiRepository.findByEmail(email);
                if (cont == null) {
                    Conturi contNou = new Conturi();
                    contNou.setEmail(email);
                    conturiRepository.save(contNou);
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/utilizator/returneazacodurireducere")
    public ResponseEntity returneazaCoduriReducere(@RequestBody String json) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("email") != null) {
                String email = nod.get("email").textValue();
                Conturi cont = conturiRepository.findByEmail(email);
                if (cont != null) {
                    return ResponseEntity.ok().body(codReducereRepository.findByEmail(email));
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/utilizator/verificacodreducere")
    public ResponseEntity verificaCodReducere(@RequestBody String json) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("email") != null && nod.get("cod") != null) {
                String email = nod.get("email").textValue();
                String codReducere = nod.get("cod").textValue();
                Conturi cont = conturiRepository.findByEmail(email);
                if (cont != null) {
                    List<CodReducere> listaCoduriReducere = codReducereRepository.findByEmail(cont.getEmail());
                    if (listaCoduriReducere.stream().filter(codReducereClient -> !codReducereClient.isFolosit()).anyMatch(codReducereClient -> codReducereClient.getCod().equals(codReducere))) {
                        Map<String, String> body = new HashMap<>();
                        body.put("sumaRedusa", String.valueOf(listaCoduriReducere.stream().filter(codReducereClient -> !codReducereClient.isFolosit()).findFirst().get().getSumaRedusa()));

                        return ResponseEntity.ok().body(body);
                    }
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/utilizator/folosestecodreducere")
    public ResponseEntity folosesteCodReducere(@RequestBody String json) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("email") != null && nod.get("cod") != null) {
                String email = nod.get("email").textValue();
                String codReducere = nod.get("cod").textValue();
                Conturi cont = conturiRepository.findByEmail(email);
                if (cont != null) {
                    List<CodReducere> listaCoduriReducere = codReducereRepository.findByEmail(cont.getEmail());
                    Optional<CodReducere> codReducereActual = listaCoduriReducere.stream().filter(codReducereClient -> !codReducereClient.isFolosit()).filter(codReducereClient -> codReducereClient.getCod().equals(codReducere)).findFirst();
                    if (codReducereActual.isPresent()) {
                        codReducereActual.get().setFolosit(true);
                        Statistici statistici = statisticiRepository.findByIdProdus(codReducereActual.get().getIdProdus());
                        statistici.setCupoaneDeReducereUtilizate(statistici.getCupoaneDeReducereUtilizate() + 1);
                        statisticiRepository.save(statistici);
                        codReducereRepository.save(codReducereActual.get());
                        return new ResponseEntity<>(HttpStatus.OK);
                    }
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/utilizator/returneazacomenziplasate")
    public ResponseEntity returneazaComenziPlasate(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("email") != null) {
                return ResponseEntity.ok().body(cosRepository.findAll().stream().
                        filter(cos -> cos.getEmail().equals(nod.get("email").textValue())).toList());
            } else {
                return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/utilizator/produsaccesat")
    public ResponseEntity produsAccesat(@RequestBody String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode nod = objectMapper.readTree(json);
            if (nod.get("idProdus") != null) {
                Statistici statistica = statisticiRepository.findByIdProdus(Long.parseLong(nod.get("idProdus").textValue()));
                statistica.setAccesari(statistica.getAccesari() + 1);
                statisticiRepository.save(statistica);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/utilizator/returneazatop")
    public ResponseEntity returneazaTopProduse() {
        try {
            Map<Statistici, Double> scor = new HashMap<>();
            List<ProdusePrioritizate> produsePrioritizate = produseProritizateRepository.findAll();
            List<Produs> prod = new LinkedList<>();
            if (produsePrioritizate.size() < 20) {
                statisticiRepository.findAll().forEach(produs -> scor.put(produs, Utilitati.calculPondere(produs.getProduseVandute(), produs.getAccesari())));
                if (scor.size() >= 2) {
                    Map<Statistici, Double> result = scor.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByValue())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> oldValue, LinkedHashMap::new));

                    List<Statistici> keys = result.keySet().stream()
                            .limit(10)
                            .toList();

                    if (!produsePrioritizate.isEmpty()) {
                        for (ProdusePrioritizate stat : produsePrioritizate) {
                            Optional<Produs> produs = produsRepository.findById(stat.getIdProdus());
                            produs.ifPresent(prod::add);
                        }
                    }
                    if (!keys.isEmpty()) {
                        for (Statistici stat : keys) {
                            Optional<Produs> produs = produsRepository.findById(stat.getIdProdus());
                            produs.ifPresent(prod::add);
                        }
                    }
                } else {
                    if (!produsePrioritizate.isEmpty()) {
                        for (ProdusePrioritizate stat : produsePrioritizate) {
                            Optional<Produs> produs = produsRepository.findById(stat.getIdProdus());
                            produs.ifPresent(prod::add);
                        }
                    }
                    if (!scor.isEmpty()) {
                        Optional<Produs> produs = produsRepository.findById(scor.keySet().stream().findFirst().get().getIdProdus());
                        produs.ifPresent(prod::add);
                    }
                }
                return ResponseEntity.ok().body(prod);
            } else {
                for (ProdusePrioritizate stat : produsePrioritizate) {
                    Optional<Produs> produs = produsRepository.findById(stat.getIdProdus());
                    produs.ifPresent(prod::add);
                }
                return ResponseEntity.ok().body(prod);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
