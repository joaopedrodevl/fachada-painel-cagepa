package com.fachada.cagepa.fachadacagepa.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IClienteJpaRepository extends JpaRepository<Cliente, UUID> {
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByTelefone(String telefone);
}
