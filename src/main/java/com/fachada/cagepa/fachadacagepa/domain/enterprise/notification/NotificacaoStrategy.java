package com.fachada.cagepa.fachadacagepa.domain.enterprise.notification;

public interface NotificacaoStrategy {
    boolean enviarNotificacao(NotificacaoConsumo notificacao);
}