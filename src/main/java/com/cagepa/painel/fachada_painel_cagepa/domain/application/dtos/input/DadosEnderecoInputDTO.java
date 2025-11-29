package com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoEndereco;

public record DadosEnderecoInputDTO(
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String estado,
    String cep,
    TipoEndereco tipoEndereco
) {}
