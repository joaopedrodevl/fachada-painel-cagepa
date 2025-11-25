package com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.CpfCnpj;
import com.cagepa.painel.fachada_painel_cagepa.domain.enums.TipoCliente;

public record DadosCadastroClienteInputDTO(
    CpfCnpj cpfCnpj,
    String nome,
    String email,
    String telefone,
    DadosEnderecoInputDTO endereco,
    TipoCliente tipoCliente
) {}
