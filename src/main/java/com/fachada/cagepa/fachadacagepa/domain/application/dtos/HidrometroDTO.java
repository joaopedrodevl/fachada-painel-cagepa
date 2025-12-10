package com.fachada.cagepa.fachadacagepa.domain.application.dtos;

import java.time.LocalDate;

public record HidrometroDTO(
        String idSha,
        LocalDate dataInstalacao,
        String status,
        Integer limiteConsumoMensalM3,
        String clienteCpfCnpj,
        EnderecoDTO enderecoInstalacao
) {

}
