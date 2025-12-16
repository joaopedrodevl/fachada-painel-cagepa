package com.fachada.cagepa.fachadacagepa.infra.persistence;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusCliente;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoCliente;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.state.ClienteState;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.state.NovoClienteState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, name = "cpf_cnpj", length = 14)
    private String cpfCnpj;

    @Column(name = "nome_completo", length = 100)
    private String nomeCompleto;

    @Column(name = "nome_fantasia", length = 100)
    private String nomeFantasia;

    @Column(name = "razao_social", length = 150)
    private String razaoSocial;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "telefone", length = 15, unique = true)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente")
    private TipoCliente tipoCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_cliente")
    private StatusCliente statusCliente;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "cliente_id")
    private List<Endereco> enderecos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Hidrometro> hidrometros = new ArrayList<>();

    @Transient
    private ClienteState clienteState = new NovoClienteState();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "updated_at")
    private LocalDateTime dataAtualizacao;

    @Column(name = "deleted_at")
    private LocalDateTime dataRemocao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public Cliente(String cpfCnpj, String nomeCompleto, String nomeFantasia, String razaoSocial,
                   String email, String telefone, Endereco endereco, TipoCliente tipoCliente) {
        this.cpfCnpj = cpfCnpj;
        this.nomeCompleto = nomeCompleto;
        this.nomeFantasia = nomeFantasia;
        this.razaoSocial = razaoSocial;
        this.email = email;
        this.telefone = telefone;
        this.enderecos = new ArrayList<>();
        if (endereco != null) {
            endereco.setCliente(this);
            this.enderecos.add(endereco);
        }
        this.tipoCliente = tipoCliente;
        this.statusCliente = StatusCliente.ATIVO;
    }

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

    @PreRemove
    protected void onRemove() {
        dataRemocao = LocalDateTime.now();
        statusCliente = StatusCliente.INATIVO;
    }

    /**
     * Seta o estado do cliente (State Pattern)
     */
    public void setClienteState(ClienteState novoEstado) {
        this.clienteState = novoEstado;
    }

    /**
     * Retorna o estado atual do cliente
     */
    public ClienteState getClienteState() {
        return this.clienteState;
    }

    /**
     * Retorna o nome apropriado do cliente baseado no tipo (PF ou PJ)
     * Para PF: retorna nomeCompleto
     * Para PJ: retorna nomeFantasia se disponível, senão razaoSocial
     */
    public String getNomeParaNotificacao() {
        // Se tem razaoSocial ou nomeFantasia preenchido, é PJ
        if ((razaoSocial != null && !razaoSocial.isEmpty()) || 
            (nomeFantasia != null && !nomeFantasia.isEmpty())) {
            return (nomeFantasia != null && !nomeFantasia.isEmpty()) ? nomeFantasia : razaoSocial;
        }
        // Caso contrário é PF
        return nomeCompleto;
    }
}
