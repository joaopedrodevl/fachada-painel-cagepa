package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories.interfaces;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastroClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Cliente;

public interface IClienteFactory {
    Cliente criarCliente(DadosCadastroClienteInputDTO dados);
}
