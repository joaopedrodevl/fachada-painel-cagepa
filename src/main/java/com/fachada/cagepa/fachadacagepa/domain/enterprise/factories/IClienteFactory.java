package com.fachada.cagepa.fachadacagepa.domain.enterprise.factories;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;

public interface IClienteFactory {
    Cliente criarClientePessoaFisica(ClientePfDTO dados);
    Cliente criarClientePessoaJuridica(ClientePjDTO dados);
}
