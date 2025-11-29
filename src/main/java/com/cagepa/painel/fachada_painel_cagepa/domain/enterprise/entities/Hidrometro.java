package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.StatusHidrometro;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "hidrometros")
public class Hidrometro {
    
    @Id
    @Column(name = "numero_serie", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_instalacao_id", nullable = false)
    private Endereco enderecoInstalacao;

    @Column(name = "data_instalacao", nullable = false)
    private LocalDate dataInstalacao;

    @Column(name = "data_remocao")
    private LocalDate dataRemocao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusHidrometro status;

    @Column(name = "limite_consumo_mensal_m3")
    private Integer limiteConsumoMensalM3;

    @Column(name = "limite_vazao_l_min")
    private Integer limiteVazaoLMin;

    @Column(name = "caminho_imagem_sha", length = 255)
    private String caminhoImagemSHA;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = StatusHidrometro.ATIVO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}