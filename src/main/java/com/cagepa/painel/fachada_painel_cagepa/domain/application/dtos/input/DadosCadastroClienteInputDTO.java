package com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoCliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.CpfCnpj;

public record DadosCadastroClienteInputDTO(
    CpfCnpj cpfCnpj,
    String nome,              
    String razaoSocial,       
    String email,
    String telefone,
    DadosEnderecoInputDTO endereco,
    TipoCliente tipoCliente
) {}
