package com.fachada.cagepa.fachadacagepa.domain.enterprise.state;

import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;

/**
 * Estado: APROVADO
 * Cliente foi aprovado e está ativo
 */
public class AprovadoClienteState implements ClienteState {

    @Override
    public void aprovar(Cliente cliente) throws StateTransitionException {
        throw new StateTransitionException(
            "Cliente já está aprovado"
        );
    }

    @Override
    public void rejeitar(Cliente cliente, String motivo) throws StateTransitionException {
        throw new StateTransitionException(
            "Não é possível rejeitar um cliente já aprovado"
        );
    }

    @Override
    public void suspender(Cliente cliente, String motivo) throws StateTransitionException {
        cliente.setClienteState(new SuspensoClienteState());
        System.out.println("🔒 Cliente suspenso. Motivo: " + motivo);
    }

    @Override
    public void reativar(Cliente cliente) throws StateTransitionException {
        throw new StateTransitionException(
            "Cliente já está ativo"
        );
    }

    @Override
    public String getStateName() {
        return "APROVADO";
    }

    @Override
    public String getDescription() {
        return "Cliente aprovado e ativo no sistema";
    }
}

