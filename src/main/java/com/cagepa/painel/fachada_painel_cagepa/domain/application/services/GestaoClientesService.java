package com.cagepa.painel.fachada_painel_cagepa.domain.application.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosAtualizarClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastroClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories.IClienteRepository;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Cliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.StatusCliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories.ClienteFactory;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.validators.ClienteValidator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestaoClientesService {
    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private ClienteValidator clienteValidator;

    @Autowired
    private ClienteFactory clienteFactory;

    public Cliente cadastrar(DadosCadastroClienteInputDTO cliente) {
        // Verifica se não tem o cliente com mesmo cpf ou cnpj cadastrado
        clienteRepository.findByCpfCnpjValor(cliente.cpfCnpj().getValor())
            .ifPresent(c -> {
                throw new IllegalArgumentException("Já existe um cliente cadastrado com o CPF/CNPJ informado.");
            });

       if (!clienteValidator.validarCadastro(cliente)) {
           throw new IllegalArgumentException("Dados do cliente inválidos. Verifique e tente novamente.");
       }
        
        return clienteRepository.salvar(clienteFactory.criarCliente(cliente));
    }

    public Cliente consultar(String cpfCnpj) {
        clienteValidator.validarCpfCnpj(cpfCnpj);
        return clienteRepository.findByCpfCnpjValor(cpfCnpj)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o CPF/CNPJ informado."));
    }

    public Cliente atualizar(String cpfCnpj, DadosAtualizarClienteInputDTO dados) {
        Cliente cliente = consultar(cpfCnpj);
        cliente.atualizarInformacoes(dados);
        return clienteRepository.salvar(cliente);
    }

    public boolean desativar(String cpfCnpj) {
        Cliente cliente = consultar(cpfCnpj);
        cliente.setStatusCliente(StatusCliente.INATIVO);
        clienteRepository.salvar(cliente);
        return true;
    }

    public List<Cliente> listar(String filtro) {
        var clientes = clienteRepository.listar();

        if (filtro == null || filtro.isBlank()) {
            return clientes;
        }

        return clientes.stream()
                .filter(c -> c.getNome().toLowerCase().contains(filtro.toLowerCase())
                        || c.getCpfCnpj().getValor().contains(filtro))
                .toList();
    }
}
