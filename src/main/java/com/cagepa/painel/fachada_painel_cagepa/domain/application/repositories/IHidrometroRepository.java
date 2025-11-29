package com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories;

import java.util.List;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Hidrometro;

public interface IHidrometroRepository extends IGenericRepository<Hidrometro, String> {
    List<Hidrometro> findByClienteId(Long clienteId);
}
