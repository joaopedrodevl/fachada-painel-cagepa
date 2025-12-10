package com.fachada.cagepa.fachadacagepa.domain.application.dtos;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoCliente;

public record ClientePjDTO(
        String cnpj,
        String nomeFantasia,
        String razaoSocial,
        String email,
        String telefone,
        EnderecoDTO endereco,
        TipoCliente tipoCliente
) {
}
