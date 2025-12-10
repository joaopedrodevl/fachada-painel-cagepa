package com.fachada.cagepa.fachadacagepa.domain.application.dtos;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoCliente;

public record ClientePfDTO(
        String cpf,
        String nome,
        String email,
        String telefone,
        EnderecoDTO endereco,
        TipoCliente tipoCliente
) {
}
