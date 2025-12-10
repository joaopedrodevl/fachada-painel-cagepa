package com.fachada.cagepa.fachadacagepa.domain.enterprise.command;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.services.ClienteService;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;

/**
 * Comando concreto para criar cliente PF
 */
public class CreateClientPfCommand implements Command {

    private final ClienteService clienteService;
    private final ClientePfDTO clienteData;
    private final String executor;
    private String createdClientId;

    public CreateClientPfCommand(ClienteService clienteService, ClientePfDTO clienteData, String executor) {
        this.clienteService = clienteService;
        this.clienteData = clienteData;
        this.executor = executor;
    }

    @Override
    public void execute() throws Exception {
        try {
            clienteService.criarClientePessoaFisica(clienteData);
            this.createdClientId = clienteData.cpf();
        } catch (ValidationException e) {
            throw new CommandExecutionException("Falha ao criar cliente PF", e);
        }
    }

    @Override
    public void undo() throws Exception {
        System.out.println("Undo: Tentativa de desfazer criacao de cliente: " + createdClientId);
    }

    @Override
    public String getDescription() {
        return String.format("Criar Cliente PF: %s (CPF: %s)",
            clienteData.nome(),
            clienteData.cpf());
    }

    @Override
    public String getExecutor() {
        return executor;
    }
}


