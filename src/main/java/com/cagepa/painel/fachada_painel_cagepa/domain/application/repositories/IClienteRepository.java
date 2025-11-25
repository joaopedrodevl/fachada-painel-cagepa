package com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories;

import java.util.Optional;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Cliente;

public interface IClienteRepository extends IGenericRepository<Cliente, Long> {
    Optional<Cliente> buscarPorCpfCnpj(String cpfCnpj);
    Cliente buscarPorEmail(String email);
    
}
