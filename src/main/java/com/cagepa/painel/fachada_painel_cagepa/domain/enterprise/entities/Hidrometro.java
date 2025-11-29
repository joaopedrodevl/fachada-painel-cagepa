package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosAtualizarHidrometroInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.StatusHidrometro;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories.EnderecoFactory;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hidrometros")
@Getter
@Setter
@EqualsAndHashCode(of = "idSha")
@NoArgsConstructor
@ToString(exclude = {"cliente", "enderecoInstalacao", "leituras"})
public class Hidrometro {
    
    @Id
    @Column(name = "id_sha", length = 50)
    private String idSha;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
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

    @OneToMany(mappedBy = "hidrometro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Leitura> leituras = new ArrayList<>();

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

    /**
     * Atualiza as informações do hidrômetro.
     */
    public void atualizarInformacoes(DadosAtualizarHidrometroInputDTO dados) {
        if (dados.caminhoImagemSha() != null) {
            this.caminhoImagemSHA = dados.caminhoImagemSha();
        }
        
        if (dados.dataInstalacao() != null && this.dataInstalacao == null) {
            this.dataInstalacao = dados.dataInstalacao();
        } else if (dados.dataInstalacao() != null && this.dataInstalacao != null) {
            throw new IllegalArgumentException("Data de instalação não pode ser alterada após ser definida.");
        }
        
        if (dados.limiteConsumoMensalM3() != null) {
            this.limiteConsumoMensalM3 = dados.limiteConsumoMensalM3();
        }
        
        if (dados.limiteVazaoLMin() != null) {
            this.limiteVazaoLMin = dados.limiteVazaoLMin();
        }
        
        if (dados.enderecoInstalacao() != null) {
            this.enderecoInstalacao = new EnderecoFactory().criarEndereco(dados.enderecoInstalacao());
        }
        
        if (dados.status() != null) {
            this.status = dados.status();
        }
    }
}