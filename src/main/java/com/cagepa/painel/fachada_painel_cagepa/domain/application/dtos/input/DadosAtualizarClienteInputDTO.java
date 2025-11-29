package com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.StatusCliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoCliente;

public record DadosAtualizarClienteInputDTO(
    String nome,              
    String razaoSocial,       
    String email,
    String telefone,
    DadosEnderecoInputDTO endereco,
    TipoCliente tipoCliente,
    StatusCliente statusCliente
) {}
