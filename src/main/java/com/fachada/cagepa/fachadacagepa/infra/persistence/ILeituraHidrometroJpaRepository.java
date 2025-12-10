package com.fachada.cagepa.fachadacagepa.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILeituraHidrometroJpaRepository extends JpaRepository<LeituraHidrometro, Long> {
    List<LeituraHidrometro> findByHidrometroIdSha(String idSha);
    LeituraHidrometro findTopByHidrometroIdShaOrderByDataLeituraDesc(String idSha);
}

