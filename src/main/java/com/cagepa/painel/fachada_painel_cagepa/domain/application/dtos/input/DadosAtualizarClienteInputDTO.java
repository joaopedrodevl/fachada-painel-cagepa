package com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoCliente;

public record DadosAtualizarClienteInputDTO(
    String nome,
    String email,
    String telefone,
    DadosEnderecoInputDTO endereco,
    TipoCliente tipoCliente
) {}
