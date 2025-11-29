package com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.StatusHidrometro;

import java.time.LocalDate;

/**
 * DTO para atualizar dados de um hidrômetro.
 * RF-004: Permite atualizar limites de consumo do hidrômetro do cliente.
 */
public record DadosAtualizarHidrometroInputDTO(
    String caminhoImagemSha,
    LocalDate dataInstalacao,
    Integer limiteConsumoMensalM3,
    Integer limiteVazaoLMin,
    DadosEnderecoInputDTO enderecoInstalacao,
    StatusHidrometro status
) {}
