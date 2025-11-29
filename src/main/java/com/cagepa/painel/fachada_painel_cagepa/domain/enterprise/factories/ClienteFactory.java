package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastroClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Cliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories.interfaces.IClienteFactory;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.Telefone;
import org.springframework.stereotype.Component;

@Component
public class ClienteFactory implements IClienteFactory {
    @Override
    public Cliente criarCliente(DadosCadastroClienteInputDTO dados) {
        return new Cliente(
            dados.cpfCnpj(),
            dados.nome(),
            dados.email(),
            new Telefone(dados.telefone()),
            new EnderecoFactory().criarEndereco(dados.endereco()),
            dados.tipoCliente()
        );
    }
}
