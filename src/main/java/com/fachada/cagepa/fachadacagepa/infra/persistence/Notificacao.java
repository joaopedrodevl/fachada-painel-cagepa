package com.fachada.cagepa.fachadacagepa.infra.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade para armazenar histórico de notificações enviadas
 * Permite retornar histórico de notificações enviadas
 */
@Entity
@Table(name = "notificacoes", indexes = {
        @Index(name = "idx_cliente_hidrometro_data", columnList = "cliente_cpf_cnpj,hidrometro_id_sha,data_envio"),
        @Index(name = "idx_data_envio", columnList = "data_envio")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cliente_cpf_cnpj", nullable = false, length = 14)
    private String clienteCpfCnpj;

    @Column(name = "hidrometro_id_sha", nullable = false, length = 50)
    private String hidrometroIdSha;

    @Column(name = "cliente_email", nullable = false)
    private String clienteEmail;

    @Column(name = "assunto", nullable = false)
    private String assunto;

    @Column(name = "mensagem", nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "percentual_consumo", nullable = false)
    private Double percentualConsumo;

    @Column(name = "limite_mensal_m3", nullable = false)
    private Integer limiteMensalM3;

    @Column(name = "consumo_atual_m3", nullable = false)
    private Integer consumoAtualM3;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;

    @Column(name = "status_envio", nullable = false, length = 20)
    private String statusEnvio; // ENVIADO, FALHA, PENDENTE

    @Column(name = "error_message")
    private String errorMessage;

    public Notificacao(String clienteCpfCnpj, String hidrometroIdSha, String clienteEmail,
                       String assunto, String mensagem, Double percentualConsumo,
                       Integer limiteMensalM3, Integer consumoAtualM3) {
        this.clienteCpfCnpj = clienteCpfCnpj;
        this.hidrometroIdSha = hidrometroIdSha;
        this.clienteEmail = clienteEmail;
        this.assunto = assunto;
        this.mensagem = mensagem;
        this.percentualConsumo = percentualConsumo;
        this.limiteMensalM3 = limiteMensalM3;
        this.consumoAtualM3 = consumoAtualM3;
        this.dataEnvio = LocalDateTime.now();
        this.statusEnvio = "PENDENTE";
    }
}

