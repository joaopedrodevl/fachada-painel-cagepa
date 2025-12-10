package com.fachada.cagepa.fachadacagepa.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEnderecoJpaRepository extends JpaRepository<Endereco, Long> {
    Optional<Endereco> findByClienteAndLogradouroAndNumeroAndComplemento(
            Cliente cliente,
            String logradouro,
            String numero,
            String complemento
    );
}
