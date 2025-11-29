package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoDocumento;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class CpfCnpj {
    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 14)
    private final String valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 4)
    private final TipoDocumento tipoDocumento;

    private CpfCnpj(String valor, TipoDocumento tipoDocumento) {
        this.valor = valor;
        this.tipoDocumento = tipoDocumento;
    }

    public static CpfCnpj create(String documento) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("CPF/CNPJ não pode ser vazio");
        }

        String documentoLimpo = documento.replaceAll("[^0-9]", "");

        if (documentoLimpo.length() == 11) {
            if (!validarCpf(documentoLimpo)) {
                throw new IllegalArgumentException("CPF inválido: " + documento);
            }
            return new CpfCnpj(documentoLimpo, TipoDocumento.CPF);
        }

        if (documentoLimpo.length() == 14) {
            if (!validarCnpj(documentoLimpo)) {
                throw new IllegalArgumentException("CNPJ inválido: " + documento);
            }
            return new CpfCnpj(documentoLimpo, TipoDocumento.CNPJ);
        }

        throw new IllegalArgumentException(
                "Documento inválido: '" + documento + "'. Deve ter 11 dígitos (CPF) ou 14 dígitos (CNPJ)."
        );
    }

    private static boolean validarCpf(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) {
            return false; 
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) primeiroDigito = 0;

        if (Character.getNumericValue(cpf.charAt(9)) != primeiroDigito) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) segundoDigito = 0;

        return Character.getNumericValue(cpf.charAt(10)) == segundoDigito;
    }

    private static boolean validarCnpj(String cnpj) {
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        int[] pesosPrimeiro = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesosPrimeiro[i];
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) primeiroDigito = 0;

        if (Character.getNumericValue(cnpj.charAt(12)) != primeiroDigito) {
            return false;
        }

        int[] pesosSegundo = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesosSegundo[i];
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) segundoDigito = 0;

        return Character.getNumericValue(cnpj.charAt(13)) == segundoDigito;
    }

    public String getValor() {
        return valor;
    }

    public TipoDocumento getTipo() {
        return tipoDocumento;
    }

    public boolean isCpf() {
        return tipoDocumento == TipoDocumento.CPF;
    }

    public boolean isCnpj() {
        return tipoDocumento == TipoDocumento.CNPJ;
    }

    public String formatado() {
        if (isCpf()) {
            return valor.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }

        return valor.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    @Override
    public String toString() {
        return formatado();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CpfCnpj that = (CpfCnpj) object;
        return valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }
}