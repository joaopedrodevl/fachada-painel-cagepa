package com.fachada.cagepa.fachadacagepa.domain.application.dtos;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoEndereco;

public record EnderecoDTO(
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String estado,
    String cep,
    TipoEndereco tipoEndereco
) { }
