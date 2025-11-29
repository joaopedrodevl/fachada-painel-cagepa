package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.interfaces;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosEnderecoInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Endereco;

public interface IEnderecoFactory {
    Endereco criarEndereco(DadosEnderecoInputDTO dados);
}
