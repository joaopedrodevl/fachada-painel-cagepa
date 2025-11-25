package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.validators;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastroClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.CpfCnpj;

import java.util.regex.Pattern;

public class ClienteValidator {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    public boolean validarCadastro(DadosCadastroClienteInputDTO dados) {
        if (dados == null) {
            return false;
        }
        
        return validarEmail(dados.email());
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
        
        return validarEmail(dados.email());

    }
}
