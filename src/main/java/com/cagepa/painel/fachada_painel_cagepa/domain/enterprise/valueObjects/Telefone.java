package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects;

import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Telefone {
    @Column(name = "telefone", nullable = false, length = 11)
    private String valor;

    private static final Pattern TELEFONE_PATTERN = Pattern.compile("^\\d{10,11}$");

    public Telefone(String valor) {
        this.valor = valor;

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Não foi possível cadastrar o cliente! Verifique os dados e tente novamente.");
        }

            String telefoneLimpo = valor.replaceAll("[^0-9]", "");

            if (!TELEFONE_PATTERN.matcher(telefoneLimpo).matches()) {
                throw new IllegalArgumentException("Não foi possível cadastrar o cliente! Verifique os dados e tente novamente.");
            }

            if (telefoneLimpo.matches("(\\d)\\1{9,10}")) {
                throw new IllegalArgumentException("Não foi possível cadastrar o cliente! Verifique os dados e tente novamente.");
            }

            int ddd = Integer.parseInt(telefoneLimpo.substring(0, 2));
            if (ddd < 11 || ddd > 99) {
                throw new IllegalArgumentException("Não foi possível cadastrar o cliente! Verifique os dados e tente novamente.");
            }

            String numeroPrincipal = telefoneLimpo.substring(2);
            if (numeroPrincipal.matches("0+")) {
                throw new IllegalArgumentException("Não foi possível cadastrar o cliente! Verifique os dados e tente novamente.");
            }

            if (numeroPrincipal.matches(".*0{5,}.*")) {
                throw new IllegalArgumentException("Não foi possível cadastrar o cliente! Verifique os dados e tente novamente.");
            }

            valor = telefoneLimpo;
        }

        public String formatarTelefone() {
            if (valor.length() == 11) {  
                return String.format("(%s) %s-%s",
                        valor.substring(0, 2),
                        valor.substring(2, 7),
                        valor.substring(7));
            } else {     
                return String.format("(%s) %s-%s",
                        valor.substring(0, 2),
                        valor.substring(2, 6),
                        valor.substring(6));
            }
        }
}