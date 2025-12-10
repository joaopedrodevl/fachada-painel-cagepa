package com.fachada.cagepa.fachadacagepa.domain.enterprise.notification.impl;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.notification.NotificacaoConsumo;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.notification.NotificacaoStrategy;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResendEmailNotificacaoStrategy implements NotificacaoStrategy {

    private static final Logger logger = LoggerFactory.getLogger("NOTIFICACAO");

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.email.from:noreply@cagepa.com.br}")
    private String emailFrom;

    @Override
    public boolean enviarNotificacao(NotificacaoConsumo notificacao) {
        try {
            if (resendApiKey == null || resendApiKey.isEmpty()) {
                logger.warn("Resend API key nao configurada. Email nao sera enviado.");
                return false;
            }

            Resend resend = new Resend(resendApiKey);

            String htmlContent = gerarHtmlEmail(notificacao);

            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(emailFrom)
                    .to(notificacao.getClienteEmail())
                    .subject("Alerta de Consumo Alto - CAGEPA")
                    .html(htmlContent)
                    .build();

            CreateEmailResponse resposta = resend.emails().send(options);

            if (resposta != null && resposta.getId() != null) {
                logger.info("Email enviado com sucesso para " + notificacao.getClienteEmail() +
                        " | Hidrometro: " + notificacao.getHidrometroId() +
                        " | ID: " + resposta.getId());
                return true;
            } else {
                logger.error("Falha ao enviar email para " + notificacao.getClienteEmail());
                return false;
            }

        } catch (ResendException e) {
            logger.error("Erro ao enviar email via Resend: " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Erro inesperado ao enviar notificacao: " + e.getMessage(), e);
            return false;
        }
    }

    private String gerarHtmlEmail(NotificacaoConsumo notificacao) {
        double consumoRestante = notificacao.getLimiteConsumo() - notificacao.getConsumoAtual();
        String colorPercentual = notificacao.getPercentualConsumo() >= 90 ? "#dc3545" : "#ff9800";

        String width = String.valueOf(Math.min(notificacao.getPercentualConsumo(), 100));
        String percentualFormatado = String.format("%.0f", notificacao.getPercentualConsumo());
        String consumoAtualFormatado = String.format("%.2f", notificacao.getConsumoAtual());
        String consumoRestanteFormatado = String.format("%.2f", consumoRestante);
        String percentualConsumo = String.format("%.1f", notificacao.getPercentualConsumo());
        String ano = String.valueOf(java.time.LocalDate.now().getYear());
        String dataNow = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        return "<!DOCTYPE html>\n" +
                "<html lang=\"pt-BR\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Alerta de Consumo Alto - CAGEPA</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "            background-color: #f5f5f5;\n" +
                "            margin: 0;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 2px 8px rgba(0,0,0,0.1);\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        .header {\n" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                "            color: white;\n" +
                "            padding: 40px 20px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .header h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 28px;\n" +
                "            font-weight: 600;\n" +
                "        }\n" +
                "        .header p {\n" +
                "            margin: 8px 0 0 0;\n" +
                "            font-size: 14px;\n" +
                "            opacity: 0.9;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 30px 20px;\n" +
                "        }\n" +
                "        .greeting {\n" +
                "            font-size: 16px;\n" +
                "            color: #333;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .alert-box {\n" +
                "            background-color: #fff3cd;\n" +
                "            border-left: 4px solid #ffc107;\n" +
                "            padding: 15px;\n" +
                "            margin: 20px 0;\n" +
                "            border-radius: 4px;\n" +
                "        }\n" +
                "        .stats {\n" +
                "            display: grid;\n" +
                "            grid-template-columns: 1fr 1fr;\n" +
                "            gap: 15px;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .stat-box {\n" +
                "            background-color: #f8f9fa;\n" +
                "            padding: 15px;\n" +
                "            border-radius: 6px;\n" +
                "            border-left: 4px solid #667eea;\n" +
                "        }\n" +
                "        .stat-label {\n" +
                "            font-size: 12px;\n" +
                "            color: #666;\n" +
                "            text-transform: uppercase;\n" +
                "            margin-bottom: 5px;\n" +
                "        }\n" +
                "        .stat-value {\n" +
                "            font-size: 20px;\n" +
                "            font-weight: 600;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .percentual-bar {\n" +
                "            background-color: #e9ecef;\n" +
                "            height: 24px;\n" +
                "            border-radius: 4px;\n" +
                "            overflow: hidden;\n" +
                "            margin: 15px 0;\n" +
                "        }\n" +
                "        .percentual-fill {\n" +
                "            height: 100%;\n" +
                "            background-color: " + colorPercentual + ";\n" +
                "            width: " + width + "%;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            color: white;\n" +
                "            font-weight: 600;\n" +
                "            font-size: 12px;\n" +
                "        }\n" +
                "        .hidrometro-info {\n" +
                "            background-color: #e8f4f8;\n" +
                "            padding: 15px;\n" +
                "            border-radius: 6px;\n" +
                "            border-left: 4px solid #17a2b8;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .hidrometro-info strong {\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .recommendations {\n" +
                "            background-color: #f0f7ff;\n" +
                "            padding: 20px;\n" +
                "            border-radius: 6px;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .recommendations h3 {\n" +
                "            margin-top: 0;\n" +
                "            color: #0056b3;\n" +
                "        }\n" +
                "        .recommendations ul {\n" +
                "            margin: 10px 0;\n" +
                "            padding-left: 20px;\n" +
                "        }\n" +
                "        .recommendations li {\n" +
                "            margin: 8px 0;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            background-color: #f8f9fa;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "            border-top: 1px solid #e9ecef;\n" +
                "            font-size: 12px;\n" +
                "            color: #666;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Alerta de Consumo Alto</h1>\n" +
                "            <p>Seu consumo de água atingiu nível elevado</p>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <div class=\"greeting\">\n" +
                "                <strong>Prezado(a) " + notificacao.getClienteNome() + ",</strong>\n" +
                "                <p>Informamos que um de seus hidrometros registrou um consumo elevado de água. Recomendamos que você verifique sua utilização para evitar surpresas na próxima fatura.</p>\n" +
                "            </div>\n" +
                "            <div class=\"alert-box\">\n" +
                "                <strong>Consumo Crítico Detectado</strong>\n" +
                "                <p>Seu consumo no mês atingiu " + percentualConsumo + "% do limite mensal.</p>\n" +
                "            </div>\n" +
                "            <div class=\"hidrometro-info\">\n" +
                "                <strong>Hidrometro:</strong> " + notificacao.getHidrometroId() + "<br>\n" +
                "                <strong>Data da Notificacao:</strong> " + dataNow + "\n" +
                "            </div>\n" +
                "            <div class=\"stats\">\n" +
                "                <div class=\"stat-box\">\n" +
                "                    <div class=\"stat-label\">Consumo Atual</div>\n" +
                "                    <div class=\"stat-value\">" + consumoAtualFormatado + " m3</div>\n" +
                "                </div>\n" +
                "                <div class=\"stat-box\">\n" +
                "                    <div class=\"stat-label\">Limite Mensal</div>\n" +
                "                    <div class=\"stat-value\">" + notificacao.getLimiteConsumo() + " m3</div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div>\n" +
                "                <strong>Progresso do Consumo:</strong>\n" +
                "                <div class=\"percentual-bar\">\n" +
                "                    <div class=\"percentual-fill\">" + percentualFormatado + "%</div>\n" +
                "                </div>\n" +
                "                <p style=\"margin: 10px 0; color: #666; font-size: 14px;\">\n" +
                "                    <strong>Consumo Restante:</strong> " + consumoRestanteFormatado + " m3\n" +
                "                </p>\n" +
                "            </div>\n" +
                "            <div class=\"recommendations\">\n" +
                "                <h3>Dicas para Economizar Água</h3>\n" +
                "                <ul>\n" +
                "                    <li>Verifique se há vazamentos nas torneiras e canos</li>\n" +
                "                    <li>Reduza o tempo de banho (5-10 minutos é o ideal)</li>\n" +
                "                    <li>Use a máquina de lavar e lavar louça com carga completa</li>\n" +
                "                    <li>Reutilize água quando possível (ex: água de enxague)</li>\n" +
                "                    <li>Repare qualquer vazamento no banheiro</li>\n" +
                "                    <li>Use a descarga do vaso sanitário adequadamente</li>\n" +
                "                </ul>\n" +
                "            </div>\n" +
                "            <div style=\"text-align: center;\">\n" +
                "                <p style=\"color: #666; font-size: 14px; margin-top: 20px;\">\n" +
                "                    Se você acredita que isso é um erro ou deseja verificar seus dados, acesse seu painel de controle.\n" +
                "                </p>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p><strong>CAGEPA - Companhia de Água e Esgotos da Paraíba</strong></p>\n" +
                "            <p>Este é um email automático. Por favor, não responda.</p>\n" +
                "            <p style=\"margin: 10px 0 0 0; color: #999;\">\n" +
                "                © " + ano + " CAGEPA. Todos os direitos reservados.\n" +
                "            </p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}

