package com.cagepa.painel.fachada_painel_cagepa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastroClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosEnderecoInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.services.GestaoClientesService;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Cliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoCliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoEndereco;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.CpfCnpj;

@Component
public class FachadaPainelCagepa {
    @Autowired
    private GestaoClientesService gestaoClientesService;

    private FachadaPainelCagepa() {}

    void inicializarSistema() {
        System.out.println("Sistema inicializado com sucesso!");
    }   

    void cadastrarCliente(
        String cpfCnpj, 
        String nome, 
        String email, 
        String telefone, 
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        String tipoCliente
    ) {
        DadosCadastroClienteInputDTO dadosCliente = new DadosCadastroClienteInputDTO(
            CpfCnpj.create(cpfCnpj),
            nome,
            email,
            telefone,
            new DadosEnderecoInputDTO(logradouro, numero, complemento, bairro, cidade, estado, cep, TipoEndereco.valueOf(tipoCliente.toUpperCase())),
            TipoCliente.valueOf(tipoCliente.toUpperCase())
        );

        Cliente cliente = gestaoClientesService.cadastrar(dadosCliente);
        System.out.println("Cliente cadastrado com sucesso: " + cliente);
    }
}
