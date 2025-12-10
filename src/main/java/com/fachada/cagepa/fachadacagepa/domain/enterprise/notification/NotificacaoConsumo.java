package com.fachada.cagepa.fachadacagepa.domain.enterprise.notification;

import com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro;

public class NotificacaoConsumo {

    private String id;
    private String clienteNome;
    private String clienteEmail;
    private String hidrometroId;
    private Double consumoAtual;
    private Integer limiteConsumo;
    private Double percentualConsumo;
    private String status;
    private String dataNotificacao;

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

    public String getId() {
        return id;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public String getHidrometroId() {
        return hidrometroId;
    }

    public Double getConsumoAtual() {
        return consumoAtual;
    }

    public Integer getLimiteConsumo() {
        return limiteConsumo;
    }

    public Double getPercentualConsumo() {
        return percentualConsumo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataNotificacao() {
        return dataNotificacao;
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

