package com.fachada.cagepa.fachadacagepa.domain.enterprise.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AuditLoggerService {

    private static final Logger logger = LoggerFactory.getLogger("AUDITORIA");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public void logOperacao(String usuario, String operacao, String descricao, String status) {
        String mensagem = String.format(
            "[%s] Usuario: %s | Operacao: %s | Descricao: %s | Status: %s",
            LocalDateTime.now().format(formatter),
            usuario,
            operacao,
            descricao,
            status
        );

        switch (status.toUpperCase()) {
            case "SUCESSO":
                logger.info(mensagem);
                break;
            case "ERRO":
                logger.error(mensagem);
                break;
            case "AVISO":
                logger.warn(mensagem);
                break;
            default:
                logger.debug(mensagem);
        }
    }

    public void logSucesso(String usuario, String operacao, String descricao) {
        logOperacao(usuario, operacao, descricao, "SUCESSO");
    }

    public void logErro(String usuario, String operacao, String descricao, String erro) {
        String mensagem = String.format(
            "[%s] Usuario: %s | Operacao: %s | Descricao: %s | Status: ERRO | Detalhes: %s",
            LocalDateTime.now().format(formatter),
            usuario,
            operacao,
            descricao,
            erro
        );
        logger.error(mensagem);
    }

    public void logAviso(String usuario, String operacao, String descricao) {
        logOperacao(usuario, operacao, descricao, "AVISO");
    }

    public void logValidacao(String campo, String erro) {
        String mensagem = String.format("Validacao falhou | Campo: %s | Erro: %s", campo, erro);
        logger.warn(mensagem);
    }

    public void logAcesso(String usuario, String acao) {
        String mensagem = String.format("[%s] Usuario: %s | Acao: %s",
            LocalDateTime.now().format(formatter),
            usuario,
            acao
        );
        logger.info(mensagem);
    }
}

