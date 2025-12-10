package com.fachada.cagepa.fachadacagepa.domain.enterprise.state;

import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;

/**
 * Estado: REJEITADO
 * Cliente foi rejeitado e não pode mais fazer operações
 */
public class RejeitadoClienteState implements ClienteState {

    @Override
    public void aprovar(Cliente cliente) throws StateTransitionException {
        throw new StateTransitionException(
            "Não é possível aprovar um cliente rejeitado"
        );
    }

    @Override
    public void rejeitar(Cliente cliente, String motivo) throws StateTransitionException {
        throw new StateTransitionException(
            "Cliente já está rejeitado"
        );
    }

    @Override
    public void suspender(Cliente cliente, String motivo) throws StateTransitionException {
        throw new StateTransitionException(
            "Não é possível suspender um cliente rejeitado"
        );
    }

    @Override
    public void reativar(Cliente cliente) throws StateTransitionException {
        throw new StateTransitionException(
            "Não é possível reativar um cliente rejeitado. Contate o suporte."
        );
    }

    @Override
    public String getStateName() {
        return "REJEITADO";
    }

    @Override
    public String getDescription() {
        return "Cliente rejeitado - não autorizado para operações";
    }
}


