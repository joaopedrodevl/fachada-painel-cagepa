package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosAtualizarClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.StatusCliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoCliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories.EnderecoFactory;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.CpfCnpj;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.Telefone;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"enderecos", "hidrometros"})
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private CpfCnpj cpfCnpj;

    @Column(name = "nome_completo", length = 100)
    private String nomeCompleto;

    @Column(name = "nome_fantasia", length = 100)
    private String nomeFantasia;

    @Column(name = "razao_social", length = 150)
    private String razaoSocial;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Embedded
    private Telefone telefone;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "cliente_id")
    private List<Endereco> enderecos = new ArrayList<>();

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

    public Cliente(CpfCnpj cpfCnpj, String nomeCompleto, String nomeFantasia, String razaoSocial, 
                   String email, Telefone telefone, Endereco endereco, TipoCliente tipoCliente) {
        this.cpfCnpj = cpfCnpj;
        this.nomeCompleto = nomeCompleto;
        this.nomeFantasia = nomeFantasia;
        this.razaoSocial = razaoSocial;
        this.email = email;
        this.telefone = telefone;
        this.enderecos = new ArrayList<>();
        if (endereco != null) {
            this.enderecos.add(endereco);
        }
        this.tipoCliente = tipoCliente;
        this.statusCliente = StatusCliente.ATIVO;
    }

    public String getNomeExibicao() {
        return cpfCnpj.isCnpj() ? nomeFantasia : nomeCompleto;
    }

    public void atualizarInformacoes(DadosAtualizarClienteInputDTO dados) {
        if (cpfCnpj.isCpf()) {
            this.nomeCompleto = dados.nome() != null ? dados.nome() : this.nomeCompleto;
        } else {
            this.nomeFantasia = dados.nome() != null ? dados.nome() : this.nomeFantasia;
            this.razaoSocial = dados.razaoSocial() != null ? dados.razaoSocial() : this.razaoSocial;
        }
        
        if (dados.email() != null) {
            this.email = dados.email();
        }
        
        if (dados.telefone() != null) {
            this.telefone = new Telefone(dados.telefone());
        }
        
        if (dados.endereco() != null) {
            Endereco novoEndereco = new EnderecoFactory().criarEndereco(dados.endereco());
            this.enderecos.clear();
            this.enderecos.add(novoEndereco);
        }
        
        if (dados.tipoCliente() != null) {
            this.tipoCliente = dados.tipoCliente();
        }
        
        if (dados.statusCliente() != null) {
            this.statusCliente = dados.statusCliente();
        }
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