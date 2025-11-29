package com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Cliente;

public interface IClienteRepository extends IGenericRepository<Cliente, Long> {
    Optional<Cliente> findByCpfCnpjValor(String cpfCnpj);
    Cliente findByEmail(String email);
    
    @Query("SELECT DISTINCT c FROM Cliente c LEFT JOIN FETCH c.hidrometros h " +
           "WHERE LOWER(c.nomeCompleto) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
           "OR LOWER(c.nomeFantasia) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
           "OR c.cpfCnpj.valor LIKE CONCAT('%', :filtro, '%') " +
           "OR LOWER(h.idSha) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    List<Cliente> buscarComFiltro(@Param("filtro") String filtro);
}
