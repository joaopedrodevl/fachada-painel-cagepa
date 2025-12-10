package com.fachada.cagepa.fachadacagepa.domain.enterprise.state;

import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;

/**
 * Estado: VALIDADO
 * Dados do cliente foram validados, aguardando aprovação
 */
public class ValidadoClienteState implements ClienteState {
    
    @Override
    public void aprovar(Cliente cliente) throws StateTransitionException {
        cliente.setClienteState(new AprovadoClienteState());
        System.out.println("Cliente transicionou para: APROVADO");
    }
    
    @Override
    public void rejeitar(Cliente cliente, String motivo) throws StateTransitionException {
        cliente.setClienteState(new RejeitadoClienteState());
        System.out.println("Cliente rejeitado. Motivo: " + motivo);
    }
    
    @Override
    public void suspender(Cliente cliente, String motivo) throws StateTransitionException {
        throw new StateTransitionException(
            "Não é possível suspender um cliente no estado VALIDADO"
        );
    }
    
    @Override
    public void reativar(Cliente cliente) throws StateTransitionException {
        throw new StateTransitionException(
            "Não é possível reativar um cliente no estado VALIDADO"
        );
    }
    
    @Override
    public String getStateName() {
        return "VALIDADO";
    }
    
    @Override
    public String getDescription() {
        return "Dados validados, aguardando aprovação final";
    }
}


