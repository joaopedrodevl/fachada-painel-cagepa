package com.cagepa.painel.fachada_painel_cagepa.domain.application.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosAtualizarClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastroClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories.IClienteRepository;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Cliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories.ClienteFactory;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.validators.ClienteValidator;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.CpfCnpj;
import com.cagepa.painel.fachada_painel_cagepa.domain.enums.StatusCliente;

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

    Cliente cadastrar(DadosCadastroClienteInputDTO cliente) {
        clienteValidator.validarCadastro(cliente);
        return clienteRepository.salvar(clienteFactory.criarCliente(cliente));
    }

    Cliente consultar(String cpfCnpj) {
        clienteValidator.validarCpfCnpj(cpfCnpj);
        return clienteRepository.buscarPorCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o CPF/CNPJ informado."));
    }

    Cliente atualizar(String cpfCnpj, DadosAtualizarClienteInputDTO dados) {
        Cliente cliente = consultar(cpfCnpj);
        cliente.atualizarInformacoes(dados);
        return clienteRepository.salvar(cliente);
    }

    boolean desativar(String cpfCnpj) {
        Cliente cliente = consultar(cpfCnpj);
        cliente.setStatusCliente(StatusCliente.INATIVO);
        clienteRepository.salvar(cliente);
        return true;
    }

    List<Cliente> listar(String filtro) {
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
