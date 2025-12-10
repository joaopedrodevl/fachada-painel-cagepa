package com.fachada.cagepa.fachadacagepa.domain.enterprise.state;

import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;

/**
 * Estado: SUSPENSO
 * Cliente foi suspenso temporariamente
 */
public class SuspensoClienteState implements ClienteState {

    @Override
    public void aprovar(Cliente cliente) throws StateTransitionException {
        throw new StateTransitionException(
            "Não é possível aprovar um cliente suspenso. Use reativar()"
        );
    }

    @Override
    public void rejeitar(Cliente cliente, String motivo) throws StateTransitionException {
        cliente.setClienteState(new RejeitadoClienteState());
        System.out.println("Cliente rejeitado. Motivo: " + motivo);
    }

    @Override
    public void suspender(Cliente cliente, String motivo) throws StateTransitionException {
        throw new StateTransitionException(
            "Cliente já está suspenso"
        );
    }

    @Override
    public void reativar(Cliente cliente) throws StateTransitionException {
        cliente.setClienteState(new AprovadoClienteState());
        System.out.println("Cliente reativado e retornou ao estado: APROVADO");
    }

    @Override
    public String getStateName() {
        return "SUSPENSO";
    }

    @Override
    public String getDescription() {
        return "Cliente temporariamente suspenso";
    }
}

