package com.fachada.cagepa.fachadacagepa.domain.enterprise.factories;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.EnderecoDTO;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Endereco;

public interface IEnderecoFactory {
    Endereco createEndereco(
            EnderecoDTO enderecoDTO
    );
}
