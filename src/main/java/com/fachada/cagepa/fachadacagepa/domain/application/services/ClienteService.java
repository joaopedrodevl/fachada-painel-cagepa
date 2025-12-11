package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.EnderecoDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.ClienteFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.EnderecoFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto.ClientePfValidator;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto.ClientePjValidator;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto.EnderecoValidator;
import com.fachada.cagepa.fachadacagepa.domain.util.CpfCnpjValidator;
import com.fachada.cagepa.fachadacagepa.infra.persistence.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final IClienteJpaRepository clienteJpaRepository;

    private final ClienteFactory clienteFactory;

    private final EnderecoFactory enderecoFactory;

    private final AuditLoggerService auditLogger;

    public ClienteService(IClienteJpaRepository clienteJpaRepository, ClienteFactory clienteFactory, EnderecoFactory enderecoFactory, IEnderecoJpaRepository enderecoJpaRepository, AuditLoggerService auditLogger) {
        this.clienteJpaRepository = clienteJpaRepository;
        this.clienteFactory = clienteFactory;
        this.enderecoFactory = enderecoFactory;
        this.auditLogger = auditLogger;
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

    /**
     * Desativar um cliente
     * @param cpfCnpj CPF/CNPJ do cliente
     * @return true se desativado com sucesso
     * @throws IllegalArgumentException se cliente não encontrado ou já inativo
     */
    @Transactional
    public boolean desativarCliente(String cpfCnpj) throws IllegalArgumentException {
        try {
            String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(cpfCnpj);

            var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

            if (!cliente.getAtivo()) {
                throw new IllegalArgumentException("Cliente já está inativo: " + cliente.getCpfCnpj());
            }

            cliente.setAtivo(false);
            clienteJpaRepository.save(cliente);

            auditLogger.logSucesso("SISTEMA", "DESATIVAR_CLIENTE", "Cliente desativado: " + cpfCnpjLimpo);
            return true;
        } catch (IllegalArgumentException e) {
            auditLogger.logErro("SISTEMA", "DESATIVAR_CLIENTE", "Erro ao desativar cliente", e.getMessage());
            throw e;
        } catch (Exception e) {
            auditLogger.logErro("SISTEMA", "DESATIVAR_CLIENTE", "Erro ao desativar cliente", e.getMessage());
            throw new RuntimeException("Erro ao desativar cliente: " + e.getMessage(), e);
        }
    }

    /**
     * Retornar todos os clientes cadastrados e ativos
     * @return Lista de clientes ativos
     */
    @Transactional(readOnly = true)
    public List<Cliente> obterTodosClientes() {
        try {
            return clienteJpaRepository.findAll()
                    .stream()
                    .filter(Cliente::getAtivo)
                    .toList();
        } catch (Exception e) {
            auditLogger.logErro("SISTEMA", "OBTER_TODOS_CLIENTES", "Erro ao obter clientes", e.getMessage());
            throw new RuntimeException("Erro ao obter clientes: " + e.getMessage(), e);
        }
    }

    /**
     * Obter dados completos de um cliente específico
     * @param cpfCnpj CPF/CNPJ do cliente
     * @return Optional com dados do cliente
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> obterClientePorCpfCnpj(String cpfCnpj) {
        try {
            String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(cpfCnpj);
            return clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                    .filter(Cliente::getAtivo);
        } catch (Exception e) {
            auditLogger.logErro("SISTEMA", "OBTER_CLIENTE", "Erro ao obter cliente", e.getMessage());
            throw new RuntimeException("Erro ao obter cliente: " + e.getMessage(), e);
        }
    }

    /**
     * Adicionar um endereço a um cliente
     * @param cpfCnpj CPF/CNPJ do cliente
     * @param endereco Dados do endereço a ser adicionado
     * @throws IllegalArgumentException se cliente não encontrado ou dados do endereço inválidos
     */
    @Transactional
    public void adicionarEnderecoCliente(String cpfCnpj, EnderecoDTO endereco) throws IllegalArgumentException {
        try {
            String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(cpfCnpj);

            var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

            // Valida os dados do endereço
            EnderecoValidator.validate(endereco);

            // Cria e associa o endereço ao cliente
            var novoEndereco = enderecoFactory.criarEndereco(
                new EnderecoDTO(endereco.logradouro(), endereco.numero(), endereco.complemento(),
                               endereco.bairro(), endereco.cidade(), endereco.estado(), endereco.cep(), endereco.tipoEndereco())
            );

            // Associa o cliente ao endereço antes de salvar
            novoEndereco.setCliente(cliente);

            cliente.getEnderecos().add(novoEndereco);
            clienteJpaRepository.save(cliente);

            auditLogger.logSucesso("SISTEMA", "ADICIONAR_ENDERECO_CLIENTE", "Endereço adicionado ao cliente: " + cpfCnpjLimpo);
        } catch (IllegalArgumentException e) {
            auditLogger.logErro("SISTEMA", "ADICIONAR_ENDERECO_CLIENTE", "Erro ao adicionar endereço ao cliente", e.getMessage());
            throw e;
        } catch (Exception e) {
            auditLogger.logErro("SISTEMA", "ADICIONAR_ENDERECO_CLIENTE", "Erro ao adicionar endereço ao cliente", e.getMessage());
            throw new RuntimeException("Erro ao adicionar endereço ao cliente: " + e.getMessage(), e);
        }
    }
}
