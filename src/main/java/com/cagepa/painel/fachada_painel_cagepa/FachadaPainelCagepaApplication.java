package com.cagepa.painel.fachada_painel_cagepa;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FachadaPainelCagepaApplication {

	public static void main(String[] args) {
		SpringApplication.run(FachadaPainelCagepaApplication.class, args);
	}

	@Bean
	CommandLineRunner run(FachadaPainelCagepa fachada) {
		return args -> fachada.inicializarSistema();
	}
}
