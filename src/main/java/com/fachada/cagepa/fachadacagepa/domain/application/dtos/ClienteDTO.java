package com.fachada.cagepa.fachadacagepa.domain.application.dtos;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoCliente;

public record ClienteDTO(
        String cpfCnpj,
        String nome,
        String nomeFantasia,
        String razaoSocial,
        String email,
        String telefone,
        EnderecoDTO endereco,
        TipoCliente tipoCliente
) {
}
