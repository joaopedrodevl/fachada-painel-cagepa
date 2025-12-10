package com.fachada.cagepa.fachadacagepa.domain.enterprise.state;

import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;

/**
 * Define comportamentos possíveis para cada estado do cliente
 */
public interface ClienteState {

    /**
     * Aprova o cliente (transição: NOVO → VALIDADO ou PENDENTE → APROVADO)
     */
    void aprovar(Cliente cliente) throws StateTransitionException;

    /**
     * Rejeita o cliente (transição: qualquer estado → REJEITADO)
     */
    void rejeitar(Cliente cliente, String motivo) throws StateTransitionException;

    /**
     * Suspende o cliente (transição: APROVADO → SUSPENSO)
     */
    void suspender(Cliente cliente, String motivo) throws StateTransitionException;

    /**
     * Reativa o cliente (transição: SUSPENSO → APROVADO)
     */
    void reativar(Cliente cliente) throws StateTransitionException;

    /**
     * Retorna nome legível do estado
     */
    String getStateName();

    /**
     * Retorna descrição do estado
     */
    String getDescription();
}

