package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.validators;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.Estados;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoEndereco;

@Component
public class EnderecoValidator {
    private static final Pattern LOGRADOURO_PATTERN = Pattern.compile(
        "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 '´`^~.,-]{2,100}$"
    );
    private static final Pattern NUMERO_PATTERN = Pattern.compile(
        "^[0-9]{1,10}$"
    );
    private static final Pattern COMPLEMENTO_PATTERN = Pattern.compile(
        "^[A-Za-zÀ-ÖØ-öø-ÿ0-9 '´`^~.,-]{0,50}$"
    );
    private static final Pattern BAIRRO_PATTERN = Pattern.compile(
        "^[A-Za-zÀ-ÖØ-öø-ÿ '´`^~.-]{2,100}$"
    );
    private static final Pattern CIDADE_PATTERN = Pattern.compile(
        "^[A-Za-zÀ-ÖØ-öø-ÿ '´`^~.-]{2,100}$"
    );
    private static final Pattern CEP_PATTERN = Pattern.compile(
        "^[0-9]{5}-?[0-9]{3}$"
    );
    
    public boolean validarLogradouro(String logradouro) {
        if (logradouro == null || logradouro.trim().isEmpty()) {
            return false;
        }
        
        return LOGRADOURO_PATTERN.matcher(logradouro).matches();
    }

    public boolean validarNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return false;
        }
        
        return NUMERO_PATTERN.matcher(numero).matches();
    }

    public boolean validarComplemento(String complemento) {
        if (complemento == null) {
            return true; // Complemento é opcional
        }
        
        return COMPLEMENTO_PATTERN.matcher(complemento).matches();
    }

    public boolean validarBairro(String bairro) {
        if (bairro == null || bairro.trim().isEmpty()) {
            return false;
        }
        
        return BAIRRO_PATTERN.matcher(bairro).matches();
    }

    public boolean validarCidade(String cidade) {
        if (cidade == null || cidade.trim().isEmpty()) {
            return false;
        }
        
        return CIDADE_PATTERN.matcher(cidade).matches();
    }

    public boolean validarEstado(String siglaEstado) {
        if (siglaEstado == null || siglaEstado.trim().isEmpty()) {
            return false;
        }
        
        for (Estados e : Estados.values()) {
            if (e.getSigla().equalsIgnoreCase(siglaEstado.trim())) {
                return true;
            }
        }
        
        return false;
    }

    public boolean validarCep(String cep) {
        if (cep == null || cep.trim().isEmpty()) {
            return false;
        }
        
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        return CEP_PATTERN.matcher(cepLimpo).matches();
    }

    public boolean validarTipoEndereco(String tipoEndereco) {
        if (tipoEndereco == null || tipoEndereco.trim().isEmpty()) {
            return false;
        }
        
        try {
            Enum.valueOf(TipoEndereco.class, tipoEndereco.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validarEnderecoCompleto(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        String tipoEndereco
    ) {
        return validarLogradouro(logradouro) &&
               validarNumero(numero) &&
               validarComplemento(complemento) &&
               validarBairro(bairro) &&
               validarCidade(cidade) &&
               validarEstado(estado) &&
               validarCep(cep) &&
               validarTipoEndereco(tipoEndereco);
    }
}
