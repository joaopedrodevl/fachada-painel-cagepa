package com.fachada.cagepa.fachadacagepa.domain.enterprise.entity;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusLeituraHidrometro;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LeituraHidrometro {
    private String clienteId;
    private Integer valor;
    private LocalDateTime dataLeitura;
    private StatusLeituraHidrometro status;

    public LeituraHidrometro(String clienteId, String valorLido, LocalDateTime dataLeitura) {
       if (clienteId == null || clienteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Cliente ID é obrigatório.");
        }
        this.clienteId = clienteId;

       if (valorLido == null || !valorLido.matches("\\d+")) {
           assert valorLido != null;
           if (valorLido.length() > 6) {
               throw new IllegalArgumentException("Valor da leitura excede o limite máximo permitido.");
           }

           if (Integer.parseInt(valorLido) < 0) {
               throw new IllegalArgumentException("Valor da leitura não pode ser negativo.");
           }

            throw new IllegalArgumentException("Valor da leitura deve ser um número válido.");
       }

        this.valor = Integer.parseInt(valorLido);

        this.dataLeitura = dataLeitura != null ? dataLeitura : LocalDateTime.now();
        this.status = StatusLeituraHidrometro.OK;
    }
}
