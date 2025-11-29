package com.cagepa.painel.fachada_painel_cagepa;

import java.time.LocalDate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class FachadaPainelCagepaApplication {

	private final FachadaPainelCagepa fachadaPainelCagepa;

	public FachadaPainelCagepaApplication(FachadaPainelCagepa fachadaPainelCagepa) {
		this.fachadaPainelCagepa = fachadaPainelCagepa;
	}

	public static void main(String[] args) {
		SpringApplication.run(FachadaPainelCagepaApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
    public void executeAfterStartup() {
        fachadaPainelCagepa.inicializarSistema();

		// Exemplo: Cadastrar Cliente Pessoa Física (CPF - Residencial)
		// var clientePF = fachadaPainelCagepa.cadastrarClientePessoaFisica(
		//     "571.371.960-04",
		//     "João Silva",
		//     "joao@email.com",
		//     "83911223344",
		//     "Rua A",
		//     "2525",
		//     null,
		//     "Bairro A",
		//     "Cidade A",
		//     "PB",
		//     "50000-000",
		//     "RESIDENCIAL"
		// );

		// Exemplo: Cadastrar Cliente Pessoa Jurídica (CNPJ - Comercial)
		// var clientePJ = fachadaPainelCagepa.cadastrarClientePessoaJuridica(
		//     "02.210.405/0001-80",
		//     "Empresa XPTO Ltda",
		//     "XPTO Comércio e Serviços Ltda",
		//     "contato@xpto.com.br",
		//     "83988776655",
		//     "Av. Principal",
		//     "1000",
		//     "Sala 10",
		//     "Centro",
		//     "João Pessoa",
		//     "PB",
		//     "58000-000",
		//     "COMERCIAL"
		// );

		// var clienteConsultado = fachadaPainelCagepa.consultarCliente("571.371.960-04");
		// var desativarCliente = fachadaPainelCagepa.desativar("571.371.960-04");
		// var clientes = fachadaPainelCagepa.listar("João");

		// var vincularHidrometro = fachadaPainelCagepa.vincularHidrometro(
		// 	"571.371.960-04",
		// 	"sha1234567890abcdef",
		// 	null,
		// 	LocalDate.now(),
		// 	100,
		// 	3,
		// 	"Rua de Instalação",
		// 	"500",
		// 	"Apto 101",
		// 	"Bairro X",
		// 	"Cidade Y",
		// 	"PB",
		// 	"58000-000",
		// 	"RESIDENCIAL"
		// );
    }
}
