package com.fachada.cagepa.fachadacagepa.domain.application.dtos;

import java.util.List;

public record ConsumoClientePeriodoDTO(
        String clienteCpfCnpj,
        String nomeCliente,
        String periodo,
        int consumoTotalM3,
        List<ConsumoHidrometroDTO> consumoPorHidrometro
) { }

