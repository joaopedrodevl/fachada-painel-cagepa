package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoEndereco;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "enderecos")
@Entity
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "logradouro", nullable = false, length = 150)
    private String logradouro;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Column(name = "complemento", length = 100)
    private String complemento;

    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade;

    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_endereco", nullable = false, length = 15)
    private TipoEndereco tipoEndereco;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "deleted_at")
    private String deletedAt;

    public Endereco(String logradouro, String numero, String complemento, 
                    String bairro, String cidade, String estado, String cep, TipoEndereco tipoEndereco) {
        validarCamposObrigatorios(logradouro, numero, bairro, cidade, estado, cep, tipoEndereco);
        validarEstado(estado);
        validarCep(cep);

        this.logradouro = logradouro.trim();
        this.numero = numero.trim();
        this.complemento = complemento != null ? complemento.trim() : null;
        this.bairro = bairro.trim();
        this.cidade = cidade.trim();
        this.estado = estado.trim().toUpperCase();
        this.cep = cep.replaceAll("[^0-9]", "");
        this.tipoEndereco = tipoEndereco;
    }

    private void validarCamposObrigatorios(String logradouro, String numero,
                                          String bairro, String cidade,
                                          String estado, String cep, TipoEndereco tipoEndereco) {
        if (logradouro == null || logradouro.trim().isEmpty()) {
            throw new IllegalArgumentException("Logradouro é obrigatório");
        }
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Número é obrigatório");
        }
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Bairro é obrigatório");
        }
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado é obrigatório");
        }
        if (cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("CEP é obrigatório");
        }
        if (tipoEndereco == null) {
            throw new IllegalArgumentException("Tipo de endereço é obrigatório");
        }
    }

    private void validarEstado(String estado) {
        if (estado.trim().length() != 2) {
            throw new IllegalArgumentException("Estado deve ter 2 caracteres");
        }
    }

    private void validarCep(String cep) {
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP deve ter 8 dígitos");
        }
    }

    public String getEnderecoCompleto() {
        StringBuilder enderecoCompleto = new StringBuilder();
        enderecoCompleto.append(logradouro).append(", ").append(numero);
        if (complemento != null && !complemento.isEmpty()) {
            enderecoCompleto.append(" - ").append(complemento);
        }
        enderecoCompleto.append(", ").append(bairro)
                        .append(" - ").append(cidade)
                        .append("/").append(estado)
                        .append(" - ").append(cep);
        return enderecoCompleto.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endereco endereco = (Endereco) o;
        return Objects.equals(logradouro, endereco.logradouro) &&
               Objects.equals(numero, endereco.numero) &&
               Objects.equals(complemento, endereco.complemento) &&
               Objects.equals(bairro, endereco.bairro) &&
               Objects.equals(cidade, endereco.cidade) &&
               Objects.equals(estado, endereco.estado) &&
               Objects.equals(cep, endereco.cep);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logradouro, numero, complemento, bairro, cidade, estado, cep);
    }

    @Override
    public String toString() {
        return logradouro + ", " + numero + 
               (complemento != null ? " - " + complemento : "") +
               ", " + bairro + " - " + cidade + "/" + estado + " - " + cep;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now().toString();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now().toString();
    }

    @PreRemove
    protected void onDelete() {
        deletedAt = LocalDateTime.now().toString();
    }
}
