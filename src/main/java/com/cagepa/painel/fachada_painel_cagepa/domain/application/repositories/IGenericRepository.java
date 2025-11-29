package com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IGenericRepository<T, ID> extends JpaRepository<T, ID> {
    /**
     * Retorna todos os registros (delegando para {@link JpaRepository#findAll()}).
     */
    default List<T> listar() {
        return findAll();
    }

    /**
     * Atualiza a entidade (delegando para {@link JpaRepository#save(Object)}).
     */
    default T atualizar(T entity) {
        return save(entity);
    }

    /**
     * Salva a entidade (delegando para {@link JpaRepository#save(Object)}).
     */
    default T salvar(T entity) {
        return save(entity);
    }

    void deleteById(ID id);
    Optional<T> findById(ID id);
}
