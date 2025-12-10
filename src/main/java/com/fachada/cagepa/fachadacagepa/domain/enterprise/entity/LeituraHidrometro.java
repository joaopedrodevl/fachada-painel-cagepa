package com.fachada.cagepa.fachadacagepa.domain.enterprise.entity;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusLeituraHidrometro;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LeituraHidrometro {
    private String hidrometroId;
    private Integer valor;
    private LocalDateTime dataLeitura;
    private StatusLeituraHidrometro status;

    public LeituraHidrometro(String hidrometroId, String valorLido, LocalDateTime dataLeitura) {
       if (hidrometroId == null || hidrometroId.trim().isEmpty()) {
            throw new IllegalArgumentException("Hidrometro ID é obrigatório.");
        }
        this.hidrometroId = hidrometroId;

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
