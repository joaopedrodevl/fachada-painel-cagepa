package com.fachada.cagepa.fachadacagepa;

import com.fachada.cagepa.fachadacagepa.facade.PainelCagepaFacade;
import com.fachada.cagepa.fachadacagepa.facade.proxy.SecurePainelCagepaFacadeProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FachadaCagepaApplication implements CommandLineRunner {

    @Autowired
    private SecurePainelCagepaFacadeProxy securePainelCagepaFacadeProxy;

    public static void main(String[] args) {
        SpringApplication.run(FachadaCagepaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        var token = securePainelCagepaFacadeProxy.login("admin", "123456");

        int op = -1;
        while (true) {
            System.out.println("Rodando");
            Thread.sleep(10000);
        }
    }
}
