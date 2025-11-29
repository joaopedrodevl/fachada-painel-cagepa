package com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.StatusHidrometro;

import java.time.LocalDate;

public record DadosCadastrarHidrometroInputDTO(
    String idSha,
    String caminhoImagemSha,
    LocalDate dataInstalacao,
    Integer limiteConsumoMensalM3,
    Integer limiteVazaoLMin,
    DadosEnderecoInputDTO enderecoInstalacao
) {
}
