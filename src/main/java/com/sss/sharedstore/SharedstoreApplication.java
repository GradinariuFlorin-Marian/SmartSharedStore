package com.sss.sharedstore;

import com.sss.sharedstore.endpoints.entities.Administrator;
import com.sss.sharedstore.endpoints.entities.StatusAplicatie;
import com.sss.sharedstore.endpoints.repositories.AdministratorRepository;
import com.sss.sharedstore.endpoints.repositories.StatusAplicatieRepository;
import com.sss.sharedstore.utils.Encriptie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SharedstoreApplication implements CommandLineRunner {

    @Autowired
    private AdministratorRepository administratorRepository;
    @Autowired
    private StatusAplicatieRepository statusAplicatieRepository;

    public static void main(String[] args) {
        SpringApplication.run(SharedstoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (administratorRepository.findByNume("sssadmin") == null) {
            Administrator admin = new Administrator();
            admin.setNume("sssadmin");

            String salt = Encriptie.genereazaSalt();
            admin.setParola(Encriptie.returneazaParolaCriptata("sssadmin", salt));
            admin.setCriptare(salt);

            administratorRepository.save(admin);
        }
        if(statusAplicatieRepository.findByTip("mentenanta") == null){
            StatusAplicatie status = new StatusAplicatie();
            status.setTip("mentenanta");
            status.setValoare("0");
            statusAplicatieRepository.save(status);
        }
    }
}
