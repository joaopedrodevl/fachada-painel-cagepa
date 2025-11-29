package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.validators;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastroClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoEndereco;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.CpfCnpj;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ClienteValidator {
    
    private final EnderecoValidator enderecoValidator;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern NOME_PATTERN = Pattern.compile(
        "^[A-Za-zÀ-ÖØ-öø-ÿ '´`^~.-]{2,100}$"
    );
    private static final Pattern TELEFON_PATTERN = Pattern.compile(
        "^\\+?[0-9]{10,15}$"
    );

    public ClienteValidator(EnderecoValidator enderecoValidator) {
        this.enderecoValidator = enderecoValidator;
    }

    public boolean validarCadastro(DadosCadastroClienteInputDTO dados) {
        if (dados == null) {
            return false;
        }
        
        return validarEmail(dados.email()) && validarNome(dados.nome()) && validarCpfCnpj(dados.cpfCnpj().getValor()) && validarTelefone(dados.telefone()) && enderecoValidator.validarEnderecoCompleto(dados.endereco().logradouro(), dados.endereco().numero(), dados.endereco().complemento(), dados.endereco().bairro(), dados.endereco().cidade(), dados.endereco().estado(), dados.endereco().cep(), dados.endereco().tipoEndereco().name()) && validarTipoCliente(dados.tipoCliente().name());
    }

    public boolean validarTipoCliente(String tipoCliente) {
        if (tipoCliente == null || tipoCliente.trim().isEmpty()) {
            return false;
        }
        
        try {
            Enum.valueOf(TipoEndereco.class, tipoCliente.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validarTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return false;
        }
        
        String cleanTelefone = telefone.replaceAll("[^0-9+]", "");

        return TELEFON_PATTERN.matcher(cleanTelefone).matches();
    }

    public boolean validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        
        return NOME_PATTERN.matcher(nome).matches();
    }
    
    public boolean validarCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.trim().isEmpty()) {
            return false;
        }
        
        String cleanCpfCnpj = cpfCnpj.replaceAll("[^0-9]", "");

        return CpfCnpj.create(cleanCpfCnpj).isCpf() || CpfCnpj.create(cleanCpfCnpj).isCnpj();
    }
    
    public boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean validarAtualizacao(DadosCadastroClienteInputDTO dados) {
        if (dados == null) {
            return false;
        }
        
        return validarEmail(dados.email()) && validarNome(dados.nome());
    }
}
