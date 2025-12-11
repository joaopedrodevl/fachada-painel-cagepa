package com.fachada.cagepa.fachadacagepa.domain.enterprise.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerenciador de notificacoes
 * Mantem registro de todas as notificacoes enviadas
 */
public class NotificacaoObserver {

    private final List<NotificacaoConsumo> notificacoesEnviadas = new ArrayList<>();

    public void registrarNotificacao(NotificacaoConsumo notificacao) {
        notificacoesEnviadas.add(notificacao);
    }

    public List<NotificacaoConsumo> obterNotificacoes() {
        return new ArrayList<>(notificacoesEnviadas);
    }

    public List<NotificacaoConsumo> obterNotificacoesDoCliente(String clienteEmail) {
        return notificacoesEnviadas.stream()
                .filter(n -> n.getClienteEmail().equals(clienteEmail))
                .toList();
    }

    public List<NotificacaoConsumo> obterNotificacoesComSucesso() {
        return notificacoesEnviadas.stream()
                .filter(n -> "ENVIADA".equals(n.getStatus()))
                .toList();
    }

    public List<NotificacaoConsumo> obterNotificacoesComErro() {
        return notificacoesEnviadas.stream()
                .filter(n -> "ERRO".equals(n.getStatus()))
                .toList();
    }

    public int getTotalNotificacoes() {
        return notificacoesEnviadas.size();
    }

    public int getTotalNotificacoesEnviadas() {
        return (int) notificacoesEnviadas.stream()
                .filter(n -> "ENVIADA".equals(n.getStatus()))
                .count();
    }

    public int getTotalNotificacoesComErro() {
        return (int) notificacoesEnviadas.stream()
                .filter(n -> "ERRO".equals(n.getStatus()))
                .count();
    }

    public void limparNotificacoes() {
        notificacoesEnviadas.clear();
    }
}

