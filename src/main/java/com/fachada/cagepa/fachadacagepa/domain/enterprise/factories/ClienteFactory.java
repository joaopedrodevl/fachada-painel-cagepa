package com.fachada.cagepa.fachadacagepa.domain.enterprise.factories;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.domain.util.CpfCnpjValidator;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Endereco;
import org.springframework.stereotype.Component;

@Component
public class ClienteFactory implements IClienteFactory{

    private final EnderecoFactory enderecoFactory;

    private ClienteFactory(EnderecoFactory enderecoFactory) {
        this.enderecoFactory = enderecoFactory;
    }

    public Cliente criarClientePessoaFisica(ClientePfDTO dados) {
        Endereco endereco = enderecoFactory.createEndereco(dados.endereco());

        String cpfSanitizado = sanitizarCpf(dados.cpf());

        return new Cliente(
                cpfSanitizado,
                dados.nome(),
                null,
                null,
                dados.email(),
                dados.telefone(),
                endereco,
                dados.tipoCliente()
        );
    }

    public Cliente criarClientePessoaJuridica(ClientePjDTO dados) {
        Endereco endereco = enderecoFactory.createEndereco(dados.endereco());

        // Sanitize CNPJ: remove formatting characters
        String cnpjSanitizado = sanitizarCnpj(dados.cnpj());

        return new Cliente(
                cnpjSanitizado,
                null,
                dados.nomeFantasia(),
                dados.razaoSocial(),
                dados.email(),
                dados.telefone(),
                endereco,
                dados.tipoCliente()
        );
    }

    private String sanitizarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser vazio");
        }

        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        if (cpfLimpo.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter exatamente 11 dígitos. Recebido: " + cpfLimpo);
        }

        if (!CpfCnpjValidator.isValidCpf(cpfLimpo)) {
            throw new IllegalArgumentException("CPF inválido. O CPF fornecido não passa na validação de dígito verificador");
        }

        return cpfLimpo;
    }

    private String sanitizarCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            throw new IllegalArgumentException("CNPJ não pode ser vazio");
        }

        String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");

        if (cnpjLimpo.length() != 14) {
            throw new IllegalArgumentException("CNPJ deve conter exatamente 14 dígitos. Recebido: " + cnpjLimpo);
        }

        if (!CpfCnpjValidator.isValidCnpj(cnpjLimpo)) {
            throw new IllegalArgumentException("CNPJ inválido. O CNPJ fornecido não passa na validação de dígito verificador");
        }

        return cnpjLimpo;
    }
}
