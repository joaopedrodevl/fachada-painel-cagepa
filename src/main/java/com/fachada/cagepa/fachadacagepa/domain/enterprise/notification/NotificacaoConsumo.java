package com.fachada.cagepa.fachadacagepa.domain.enterprise.notification;

import lombok.Getter;
import lombok.Setter;

@Getter
public class NotificacaoConsumo {

    private final String id;
    private final String clienteNome;
    private final String clienteEmail;
    private final String hidrometroId;
    private final Double consumoAtual;
    private final Integer limiteConsumo;
    private final Double percentualConsumo;
    @Setter
    private String status;
    private final String dataNotificacao;

    public NotificacaoConsumo(String clienteNome, String clienteEmail, String hidrometroId,
                              Double consumoAtual, Integer limiteConsumo, Double percentualConsumo) {
        this.id = java.util.UUID.randomUUID().toString();
        this.clienteNome = clienteNome;
        this.clienteEmail = clienteEmail;
        this.hidrometroId = hidrometroId;
        this.consumoAtual = consumoAtual;
        this.limiteConsumo = limiteConsumo;
        this.percentualConsumo = percentualConsumo;
        this.status = "ENVIADA";
        this.dataNotificacao = java.time.LocalDateTime.now().toString();
    }

    @Override
    public String toString() {
        return String.format(
            "NotificacaoConsumo{id='%s', cliente='%s', email='%s', hidrometro='%s', " +
            "consumo=%.2f m3, limite=%d m3, percentual=%.2f%%, status='%s', data='%s'}",
            id, clienteNome, clienteEmail, hidrometroId, consumoAtual,
            limiteConsumo, percentualConsumo, status, dataNotificacao
        );
    }
}

