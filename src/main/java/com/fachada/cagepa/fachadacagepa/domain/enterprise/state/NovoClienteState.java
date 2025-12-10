package com.fachada.cagepa.fachadacagepa.domain.enterprise.state;

import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;

/**
 * Estado: NOVO
 * Cliente acabou de ser criado, aguardando validação
 */
public class NovoClienteState implements ClienteState {

    @Override
    public void aprovar(Cliente cliente) throws StateTransitionException {
        cliente.setClienteState(new ValidadoClienteState());
        System.out.println("Cliente aprovado para o estado: VALIDADO");
    }

    @Override
    public void rejeitar(Cliente cliente, String motivo) throws StateTransitionException {
        cliente.setClienteState(new RejeitadoClienteState());
        System.out.println("Cliente rejeitado. Motivo: " + motivo);
    }

    @Override
    public void suspender(Cliente cliente, String motivo) throws StateTransitionException {
        throw new StateTransitionException(
            "Não é possível suspender um cliente no estado NOVO"
        );
    }

    @Override
    public void reativar(Cliente cliente) throws StateTransitionException {
        throw new StateTransitionException(
            "Não é possível reativar um cliente no estado NOVO"
        );
    }

    @Override
    public String getStateName() {
        return "NOVO";
    }

    @Override
    public String getDescription() {
        return "Cliente recém-criado, aguardando validação de dados";
    }
}

