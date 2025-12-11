package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.infra.persistence.Notificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes Unitários para NotificacaoPersistenciaService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificacaoPersistenciaServiceTest {

    @Autowired
    private NotificacaoPersistenciaService notificacaoPersistenciaService;

    private String cpfCnpj;
    private String idSha;
    private String email;

    @BeforeEach
    void setUp() {
        cpfCnpj = "12345678910";
        idSha = "SHA001";
        email = "joao@email.com";
    }

    @Test
    @DisplayName("UT-NOT-001: Deve salvar notificação corretamente")
    void testSalvarNotificacao() {
        // Arrange
        Notificacao notificacao = new Notificacao(
            cpfCnpj,
            idSha,
            email,
            "Alerta de Consumo",
            "Você atingiu 75% do limite",
            75.0,
            100,
            75
        );

        // Act
        Notificacao notificacaoSalva = notificacaoPersistenciaService.salvarNotificacao(notificacao);

        // Assert
        assertThat(notificacaoSalva.getId()).isNotNull();
        assertThat(notificacaoSalva.getStatusEnvio()).isEqualTo("PENDENTE");
        assertThat(notificacaoSalva.getDataEnvio()).isNotNull();
        assertThat(notificacaoSalva.getClienteCpfCnpj()).isEqualTo(cpfCnpj);
    }

    @Test
    @DisplayName("UT-NOT-002: Deve impedir notificação duplicada no mesmo dia")
    void testNaoPermitirNotificacaoDuplicadaHoje() {
        // Arrange
        Notificacao notif1 = new Notificacao(
            cpfCnpj, idSha, email, "Alerta", "Mensagem", 75.0, 100, 75
        );
        notificacaoPersistenciaService.salvarNotificacao(notif1);

        // Act
        boolean podeEnviar = notificacaoPersistenciaService.podeEnviarNotificacao(cpfCnpj, idSha);

        // Assert
        assertThat(podeEnviar).isFalse();
    }

    @Test
    @DisplayName("UT-NOT-003: Deve retornar histórico de notificações por cliente")
    void testObterHistoricoNotificacoes() {
        // Arrange
        for (int i = 1; i <= 3; i++) {
            Notificacao notif = new Notificacao(
                cpfCnpj, "SHA00" + i, email,
                "Alerta " + i, "Mensagem " + i, 75.0, 100, 75
            );
            notificacaoPersistenciaService.salvarNotificacao(notif);
        }

        // Act
        List<Notificacao> historico = notificacaoPersistenciaService.obterHistoricoNotificacoesCliente(cpfCnpj);

        // Assert
        assertThat(historico).hasSize(3);
        assertThat(historico).isSortedAccordingTo((n1, n2) -> n2.getDataEnvio().compareTo(n1.getDataEnvio()));
    }
}

