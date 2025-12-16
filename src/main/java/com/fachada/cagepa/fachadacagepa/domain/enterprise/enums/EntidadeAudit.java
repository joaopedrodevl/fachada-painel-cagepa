package com.fachada.cagepa.fachadacagepa.domain.enterprise.enums;

public enum EntidadeAudit {
    CLIENTE("Cliente"),
    HIDROMETRO("Hidrometro"),
    LEITURA("Leitura de Hidrometro"),
    NOTIFICACAO("Notificacao"),
    ADMIN("Administrador"),
    ENDERECO("Endereco");

    private final String descricao;

    EntidadeAudit(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
