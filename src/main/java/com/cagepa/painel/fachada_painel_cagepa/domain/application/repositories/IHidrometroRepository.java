package com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories;

import java.util.List;
import java.util.Optional;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Hidrometro;

public interface IHidrometroRepository extends IGenericRepository<Hidrometro, String> {
    Optional<Hidrometro> buscarPorId(String numeroSerie);
    List<Hidrometro> buscarPorCliente();
}
