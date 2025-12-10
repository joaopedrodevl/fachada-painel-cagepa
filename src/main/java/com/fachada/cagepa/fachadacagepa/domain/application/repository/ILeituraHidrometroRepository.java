package com.fachada.cagepa.fachadacagepa.domain.application.repository;

import com.fachada.cagepa.fachadacagepa.infra.persistence.LeituraHidrometro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ILeituraHidrometroRepository extends IGenericRepository<LeituraHidrometro, Long> {
    List<LeituraHidrometro> findByShaId(String shaId);
    Double calcularConsumoMensalHidrometro(String idHidrometro, int ano, int mes);
}
