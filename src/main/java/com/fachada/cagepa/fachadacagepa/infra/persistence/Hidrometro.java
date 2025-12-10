package com.fachada.cagepa.fachadacagepa.infra.persistence;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusHidrometro;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusLeituraHidrometro;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hidrometros")
@Getter
@Setter
@EqualsAndHashCode(of = "idSha")
@NoArgsConstructor
@AllArgsConstructor
public class Hidrometro {
    @Id
    @Column(name = "id_sha", length = 50)
    private String idSha;

    @Column(name = "data_instalacao", nullable = false)
    private LocalDate dataInstalacao;

    @Column(name = "data_remocao")
    private LocalDate dataRemocao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusHidrometro status;

    @Column(name = "limite_consumo_mensal_m3")
    private Integer limiteConsumoMensalM3;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "endereco_instalacao_id", nullable = false)
    private Endereco enderecoInstalacao;

    @OneToMany(mappedBy = "hidrometro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LeituraHidrometro> leituras = new ArrayList<>();

    public Hidrometro(String idSha, LocalDate localDate, Object o, String status, Integer integer) {
        this.idSha = idSha;
        this.dataInstalacao = localDate;
        this.status = StatusHidrometro.valueOf(status);
        this.limiteConsumoMensalM3 = integer;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
