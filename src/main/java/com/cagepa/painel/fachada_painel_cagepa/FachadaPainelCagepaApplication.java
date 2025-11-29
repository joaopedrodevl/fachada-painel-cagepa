package com.cagepa.painel.fachada_painel_cagepa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class FachadaPainelCagepaApplication {

	private FachadaPainelCagepa fachadaPainelCagepa;

	public FachadaPainelCagepaApplication(FachadaPainelCagepa fachadaPainelCagepa) {
		this.fachadaPainelCagepa = fachadaPainelCagepa;
	}

	public static void main(String[] args) {
		SpringApplication.run(FachadaPainelCagepaApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
    public void executeAfterStartup() {
        fachadaPainelCagepa.inicializarSistema();

		// Teste cadastro cliente
		fachadaPainelCagepa.cadastrarCliente("571.371.960-04", "João", "joao@email.com", "83911223344", "Rua A", "2525", null, "Bairro A", "Cidade A", "PB", "50000-000", "COMERCIAL");
    }
}
