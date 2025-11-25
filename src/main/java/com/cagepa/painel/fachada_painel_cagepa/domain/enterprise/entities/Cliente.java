package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosAtualizarClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories.EnderecoFactory;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.CpfCnpj;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.Telefone;
import com.cagepa.painel.fachada_painel_cagepa.domain.enums.StatusCliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enums.TipoCliente;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private CpfCnpj cpfCnpj;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Embedded
    private Telefone telefone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente")
    private TipoCliente tipoCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_cliente")
    private StatusCliente statusCliente;    

    @Column(name = "created_at", nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "updated_at")
    private LocalDateTime dataAtualizacao;

    @Column(name = "deleted_at")
    private LocalDateTime dataRemocao;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Hidrometro> hidrometros = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        if (statusCliente == null) {
            statusCliente = StatusCliente.ATIVO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public Cliente(CpfCnpj cpfCnpj, String nome, String email, Telefone telefone, Endereco endereco, TipoCliente tipoCliente) {
        this.cpfCnpj = cpfCnpj;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.tipoCliente = tipoCliente;
        this.statusCliente = StatusCliente.ATIVO;
    }

    public void atualizarInformacoes(DadosAtualizarClienteInputDTO dados) {
        this.nome = dados.nome() != null ? dados.nome() : this.nome;
        this.email = dados.email() != null ? dados.email() : this.email;
        this.telefone = new Telefone(dados.telefone()) != null ? new Telefone(dados.telefone()) : this.telefone;
        this.endereco = new EnderecoFactory().criarEndereco(dados.endereco()) != null ? new EnderecoFactory().criarEndereco(dados.endereco()) : this.endereco;
        this.tipoCliente = dados.tipoCliente() != null ? dados.tipoCliente() : this.tipoCliente;
    }

    public void adicionarHidrometro(Hidrometro hidrometro) {
        hidrometros.add(hidrometro);
        hidrometro.setCliente(this);
    }

    public void removerHidrometro(Hidrometro hidrometro) {
        hidrometros.remove(hidrometro);
        hidrometro.setCliente(null);
    }

    public void marcarComoRemovido() {
        this.dataRemocao = LocalDateTime.now();
        this.statusCliente = StatusCliente.INATIVO;
    }

    public boolean isRemovido() {
        return dataRemocao != null;
    }
}