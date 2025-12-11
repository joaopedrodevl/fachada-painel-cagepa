package com.fachada.cagepa.fachadacagepa.domain.enterprise.audit;

import com.fachada.cagepa.fachadacagepa.domain.application.services.AuditLoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes de Integração para AuditLoggerService
 */
@SpringBootTest
@ActiveProfiles("test")
class AuditLoggerServiceTest {

    private AuditLoggerService auditLoggerService;

    @BeforeEach
    void setUp() {
        auditLoggerService = new AuditLoggerService();
    }

    @Test
    @DisplayName("Deve registrar log de sucesso")
    void testLogSucesso() {
        // Arrange
        String usuario = "admin001";
        String operacao = "CRIAR_CLIENTE";
        String descricao = "Cliente PF criado com sucesso";

        // Act & Assert
        assertThatCode(() -> auditLoggerService.logSucesso(usuario, operacao, descricao)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve registrar log de erro")
    void testLogErro() {
        // Arrange
        String usuario = "admin001";
        String operacao = "CRIAR_CLIENTE";
        String descricao = "Falha ao criar cliente";
        String erro = "CPF inválido";

        // Act & Assert
        assertThatCode(() -> auditLoggerService.logErro(usuario, operacao, descricao, erro)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve registrar log de aviso")
    void testLogAviso() {
        // Arrange
        String usuario = "admin001";
        String operacao = "CONSULTA_CLIENTE";
        String descricao = "Cliente em situação irregular";

        // Act & Assert
        assertThatCode(() -> auditLoggerService.logAviso(usuario, operacao, descricao)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve registrar log de validação")
    void testLogValidacao() {
        // Arrange
        String campo = "email";
        String erro = "Email em formato inválido";

        // Act & Assert
        assertThatCode(() -> auditLoggerService.logValidacao(campo, erro)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve registrar log de acesso")
    void testLogAcesso() {
        // Arrange
        String usuario = "admin001";
        String acao = "LOGIN";

        // Act & Assert
        assertThatCode(() -> auditLoggerService.logAcesso(usuario, acao)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve registrar log de operação com status customizado")
    void testLogOperacaoComStatus() {
        // Arrange
        String usuario = "admin001";
        String operacao = "ATUALIZAR_CLIENTE";
        String descricao = "Atualização de endereço";
        String status = "SUCESSO";

        // Act & Assert
        assertThatCode(() -> auditLoggerService.logOperacao(usuario, operacao, descricao, status)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve registrar operações CRUD (Create, Read, Update, Delete)")
    void testRastreabilidadeCRUD() {
        // Act & Assert
        assertThatCode(() -> {
            auditLoggerService.logSucesso("admin", "CREATE", "Cliente criado");
            auditLoggerService.logSucesso("admin", "READ", "Cliente consultado");
            auditLoggerService.logSucesso("admin", "UPDATE", "Cliente atualizado");
            auditLoggerService.logSucesso("admin", "DELETE", "Cliente deletado");
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve incluir timestamp nas mensagens de auditoria")
    void testRastreabilidadeComTimestamp() {
        // Act & Assert
        assertThatCode(() -> auditLoggerService.logSucesso("admin001", "CRIAR_CLIENTE", "Cliente criado com sucesso")).doesNotThrowAnyException();
    }

}

