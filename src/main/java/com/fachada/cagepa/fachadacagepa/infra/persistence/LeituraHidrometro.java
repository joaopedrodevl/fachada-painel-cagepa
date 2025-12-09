package com.fachada.cagepa.fachadacagepa.infra.persistence;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusLeituraHidrometro;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="leitura_hidrometro")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class LeituraHidrometro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id")
    private String clienteId;

    @Column(name = "valor")
    private int valor;

    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusLeituraHidrometro status;
}
