package com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IGenericRepository<T, ID> extends JpaRepository<T, ID> {
    Optional<T> findById(ID id);
    List<T> listar();
    void deleteById(ID id);
    T atualizar(T entity);
    T salvar(T entity);
}
