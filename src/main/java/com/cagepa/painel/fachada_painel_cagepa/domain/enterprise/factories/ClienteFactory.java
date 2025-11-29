package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastroClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Cliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Endereco;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoCliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.interfaces.IClienteFactory;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.Telefone;
import org.springframework.stereotype.Component;

@Component
public class ClienteFactory implements IClienteFactory {
    
    private final EnderecoFactory enderecoFactory;

    public ClienteFactory() {
        this.enderecoFactory = new EnderecoFactory();
    }

    @Override
    public Cliente criarCliente(DadosCadastroClienteInputDTO dados) {
        if (dados.cpfCnpj().isCpf()) {
            return criarClientePessoaFisica(dados);
        } else {
            return criarClientePessoaJuridica(dados);
        }
    }

    /**
     * Cria um cliente Pessoa Física (CPF).
     * Utilizado para clientes residenciais e comerciais pequenos.
     */
    public Cliente criarClientePessoaFisica(DadosCadastroClienteInputDTO dados) {
        validarPessoaFisica(dados);
        
        Endereco endereco = enderecoFactory.criarEndereco(dados.endereco());
        
        return new Cliente(
            dados.cpfCnpj(),
            dados.nome(),
            null,         
            null,         
            dados.email(),
            new Telefone(dados.telefone()),
            endereco,
            dados.tipoCliente()
        );
    }

    /**
     * Cria um cliente Pessoa Jurídica (CNPJ).
     * Utilizado para clientes comerciais e industriais.
     */
    public Cliente criarClientePessoaJuridica(DadosCadastroClienteInputDTO dados) {
        validarPessoaJuridica(dados);
        
        Endereco endereco = enderecoFactory.criarEndereco(dados.endereco());
        
        return new Cliente(
            dados.cpfCnpj(),
            null,                    
            dados.nome(),            
            dados.razaoSocial(),     
            dados.email(),
            new Telefone(dados.telefone()),
            endereco,
            dados.tipoCliente()
        );
    }

    private void validarPessoaFisica(DadosCadastroClienteInputDTO dados) {
        if (!dados.cpfCnpj().isCpf()) {
            throw new IllegalArgumentException("Documento deve ser CPF para pessoa física");
        }
        
        if (dados.nome() == null || dados.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome completo é obrigatório para pessoa física");
        }
        
        if (dados.tipoCliente() == TipoCliente.INDUSTRIAL) {
            throw new IllegalArgumentException("CPF não pode ser usado para cliente industrial");
        }
    }

    private void validarPessoaJuridica(DadosCadastroClienteInputDTO dados) {
        if (!dados.cpfCnpj().isCnpj()) {
            throw new IllegalArgumentException("Documento deve ser CNPJ para pessoa jurídica");
        }
        
        if (dados.nome() == null || dados.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome fantasia é obrigatório para pessoa jurídica");
        }
        
        if (dados.tipoCliente() == TipoCliente.RESIDENCIAL) {
            throw new IllegalArgumentException("CNPJ não pode ser usado para cliente residencial");
        }
    }
}
