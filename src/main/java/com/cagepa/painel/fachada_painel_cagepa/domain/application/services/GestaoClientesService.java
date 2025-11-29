package com.cagepa.painel.fachada_painel_cagepa.domain.application.services;

import java.util.List;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories.IHidrometroRepository;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Hidrometro;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.StatusHidrometro;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.CpfCnpj;
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
    private IHidrometroRepository hidrometroRepository;

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
        var cpfCnpjLimpo = CpfCnpj.create(cpfCnpj).getValor();
        return clienteRepository.findByCpfCnpjValor(cpfCnpjLimpo)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o CPF/CNPJ informado."));
    }

    public Cliente atualizar(String cpfCnpj, DadosAtualizarClienteInputDTO dados) {
        Cliente cliente = consultar(cpfCnpj);
        cliente.atualizarInformacoes(dados);
        return clienteRepository.salvar(cliente);
    }

    /**
     * Desativa logicamente um cliente.
     */
    public boolean desativar(String cpfCnpj) {
        Cliente cliente = consultar(cpfCnpj);
        
        cliente.setStatusCliente(StatusCliente.INATIVO);
        clienteRepository.salvar(cliente);
        
        List<Hidrometro> hidrometros = hidrometroRepository.findByClienteId(cliente.getId());
        for (Hidrometro hidrometro : hidrometros) {
            hidrometro.setStatus(StatusHidrometro.DESATIVADO);
            hidrometroRepository.salvar(hidrometro);
        }
        
        return true;
    }

    /*
    * Filtro procura por nome, cpfCnpj ou id do Hidrometro
    * */
    public List<Cliente> listar(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return clienteRepository.listar();
        }
        
        String filtroLimpo = filtro.replaceAll("[.\\-/]", "").trim();
        
        return clienteRepository.buscarComFiltro(filtroLimpo);
    }
}
