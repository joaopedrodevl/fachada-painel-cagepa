package com.fachada.cagepa.fachadacagepa;

import com.fachada.cagepa.fachadacagepa.facade.PainelCagepaFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FachadaCagepaApplication implements CommandLineRunner {

    @Autowired
    private PainelCagepaFacade painelCagepaFacade;

    public static void main(String[] args) {
        SpringApplication.run(FachadaCagepaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String token = painelCagepaFacade.authenticate("admin", "admin") ? "Token-Valido" : null;
        painelCagepaFacade.processarImagens(token);
    }
}
