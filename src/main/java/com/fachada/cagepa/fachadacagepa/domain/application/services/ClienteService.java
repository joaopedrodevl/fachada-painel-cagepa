package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.ClienteFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto.ClientePfValidator;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto.ClientePjValidator;
import com.fachada.cagepa.fachadacagepa.domain.util.CpfCnpjValidator;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.audit.AuditLoggerService;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IClienteJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {
    private final IClienteJpaRepository clienteJpaRepository;

    private final ClienteFactory clienteFactory;

    @Autowired
    private AuditLoggerService auditLogger;

    public ClienteService(IClienteJpaRepository clienteJpaRepository, ClienteFactory clienteFactory) {
        this.clienteJpaRepository = clienteJpaRepository;
        this.clienteFactory = clienteFactory;
    }

    @Transactional
    public void criarClientePessoaFisica(ClientePfDTO cliente) throws ValidationException {
        try {
            // Valida os dados do cliente
            ClientePfValidator.validate(cliente);

            // Limpa CPF antes de processar
            String cpfLimpo = CpfCnpjValidator.cleanCpfCnpj(cliente.cpf());

            var existsCpf = clienteJpaRepository.findByCpfCnpj(cpfLimpo);
            var existsEmail = clienteJpaRepository.findByEmail(cliente.email());
            var existsTelefone = clienteJpaRepository.findByTelefone(cliente.telefone());
            if (existsCpf.isPresent() || existsEmail.isPresent() || existsTelefone.isPresent()) {
                throw new IllegalArgumentException("Cliente com CPF/Email/Telefone ja existe");
            }

            // Cria cliente com CPF limpo
            var c = clienteFactory.criarClientePessoaFisica(
                new ClientePfDTO(cpfLimpo, cliente.nome(), cliente.email(), cliente.telefone(),
                               cliente.endereco(), cliente.tipoCliente())
            );
            clienteJpaRepository.save(c);

            // Log de auditoria
            auditLogger.logSucesso("SISTEMA", "CRIAR_CLIENTE_PF", "Cliente PF criado: " + cliente.nome() + " (CPF: " + cpfLimpo + ")");
        } catch (ValidationException e) {
            auditLogger.logErro("SISTEMA", "CRIAR_CLIENTE_PF", "Falha na validacao do cliente PF", e.getMessage());
            throw e;
        } catch (Exception e) {
            auditLogger.logErro("SISTEMA", "CRIAR_CLIENTE_PF", "Erro ao criar cliente PF", e.getMessage());
            throw new RuntimeException("Erro ao criar cliente PF", e);
        }
    }

    @Transactional
    public void criarClientePessoaJuridica(ClientePjDTO cliente) throws ValidationException {
        try {
            // Valida os dados do cliente
            ClientePjValidator.validate(cliente);

            // Limpa CNPJ antes de processar
            String cnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(cliente.cnpj());

            var existsCnpj = clienteJpaRepository.findByCpfCnpj(cnpjLimpo);
            var existsEmail = clienteJpaRepository.findByEmail(cliente.email());
            var existsTelefone = clienteJpaRepository.findByTelefone(cliente.telefone());
            if (existsCnpj.isPresent() || existsEmail.isPresent() || existsTelefone.isPresent()) {
                throw new IllegalArgumentException("Cliente com CPF/Email/Telefone ja existe");
            }

            // Cria cliente com CNPJ limpo
            var c = clienteFactory.criarClientePessoaJuridica(
                new ClientePjDTO(cnpjLimpo, cliente.nomeFantasia(), cliente.razaoSocial(),
                               cliente.email(), cliente.telefone(), cliente.endereco(), cliente.tipoCliente())
            );
            clienteJpaRepository.save(c);

            // Log de auditoria
            auditLogger.logSucesso("SISTEMA", "CRIAR_CLIENTE_PJ", "Cliente PJ criado: " + cliente.nomeFantasia() + " (CNPJ: " + cnpjLimpo + ")");
        } catch (ValidationException e) {
            auditLogger.logErro("SISTEMA", "CRIAR_CLIENTE_PJ", "Falha na validacao do cliente PJ", e.getMessage());
            throw e;
        } catch (Exception e) {
            auditLogger.logErro("SISTEMA", "CRIAR_CLIENTE_PJ", "Erro ao criar cliente PJ", e.getMessage());
            throw new RuntimeException("Erro ao criar cliente PJ", e);
        }
    }
}
