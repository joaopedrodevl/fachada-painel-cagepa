package com.fachada.cagepa.fachadacagepa.domain.enterprise.enums;

public enum OperacaoAudit {
    CREATE("CRIAR"),
    READ("LER"),
    UPDATE("ATUALIZAR"),
    DELETE("DELETAR");

    private final String descricao;

    OperacaoAudit(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
