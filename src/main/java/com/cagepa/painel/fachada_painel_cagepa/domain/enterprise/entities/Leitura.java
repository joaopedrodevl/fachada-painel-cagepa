package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "consumos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Leitura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hidrometro_id_sha", nullable = false)
    private Hidrometro hidrometro;

    @Column(name = "timestamp_leitura", nullable = false)
    private LocalDateTime timestampLeitura;

    @Column(name = "vazao_l_min", nullable = false)
    private Integer vazaoLMin;

    @Column(name = "volume_total_litros", nullable = false)
    private Integer volumeTotalLitros;

    @Column(name = "imagem_origem_path", length = 255)
    private String imagemOrigemPath;
}
