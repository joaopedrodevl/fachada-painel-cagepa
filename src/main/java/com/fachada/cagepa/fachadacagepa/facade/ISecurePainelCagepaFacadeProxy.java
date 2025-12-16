package com.fachada.cagepa.fachadacagepa.facade;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoClientePeriodoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoHidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.HidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Notificacao;
import com.fachada.cagepa.fachadacagepa.infra.persistence.AuditEntry;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface ISecurePainelCagepaFacadeProxy {
    String login(String username, String password) throws IOException;
    boolean criarAdmin(String token, String username, String password);
    ClientePfDTO criarClientePf(String token, String cpf,
                                String nome,
                                String email,
                                String telefone,
                                String logradouro,
                                String numero,
                                String complemento,
                                String bairro,
                                String cidade,
                                String estado,
                                String cep,
                                String tipoEndereco,
                                String tipoCliente) throws ValidationException;
    ClientePjDTO criarClientePj(String token, String cnpj,
                                String nomeFantasia,
                                String razaoSocial,
                                String email,
                                String telefone,
                                String logradouro,
                                String numero,
                                String complemento,
                                String bairro,
                                String cidade,
                                String estado,
                                String cep,
                                String tipoEndereco,
                                String tipoCliente) throws ValidationException;
    HidrometroDTO registrarHidrometro(String token,
                                      String idSha,
                                      LocalDate dataInstalacao,
                                      String status,
                                      Integer limiteConsumoMensalM3,
                                      String clienteCpfCnpj,
                                      String logradouro,
                                      String numero,
                                      String complemento,
                                      String bairro,
                                      String cidade,
                                      String estado,
                                      String cep,
                                      String tipoEndereco) throws ValidationException;

    ConsumoClientePeriodoDTO obterConsumoDiario(String token, String clienteCpfCnpj) throws ValidationException;
    ConsumoClientePeriodoDTO obterConsumoSemanal(String token, String clienteCpfCnpj) throws ValidationException;
    ConsumoClientePeriodoDTO obterConsumoMensal(String token, String clienteCpfCnpj) throws ValidationException;
    ConsumoClientePeriodoDTO obterConsumoAnual(String token, String clienteCpfCnpj) throws ValidationException;

    // RF-032: Retorna o consumo individual de cada hidrômetro do cliente
    List<ConsumoHidrometroDTO> obterConsumoIndividualPorHidrometro(String token, String clienteCpfCnpj) throws ValidationException;

    // RF-033: Retorna a soma do consumo de todos os hidrômetros do cliente
    int obterConsumoTotalCliente(String token, String clienteCpfCnpj) throws ValidationException;

    String obterHistoricoAuditoria(String token);
    boolean limparHistoricoAuditoria(String token);

    String exibirConfiguracaoAtual(String token);
    boolean configurarDiretorioImagens(String token, String novoDir);
    boolean configurarCaminhoTesseract(String token, String novoPath);

    void verificarENotificarConsumoAlto(String token);
    String obterRelatorioNotificacoes(String token);
    boolean alterarLimiarNotificacao(String token, double novoLimiar);

    boolean desativarAdmin(String token, String username);
    List<Cliente> listarClientes(String token);

    Cliente obterClientePorCpfCnpj(String token, String cpfCnpj);

    boolean adicionarEnderecoCliente(String token, String cpfCnpj, 
                                     String logradouro, String numero, String complemento,
                                     String bairro, String cidade, String estado, String cep);

    List<Hidrometro> obterHidrometrosPorCliente(String token, String clienteCpfCnpj);

    boolean alterarStatusHidrometro(String token, String shaId, boolean ativo);

    // RF-021: Buscar um hidrômetro pelo identificador SHA
    Hidrometro obterHidrometroPorSha(String token, String idSha) throws ValidationException;

    // RF-036: Retorna os emails enviados para clientes que ultrapassaram >= 70% do limite
    List<Notificacao> obterEmailsNotificacoes(String token) throws ValidationException;

    // RF-037: Retorna histórico de notificações enviadas
    List<Notificacao> obterHistoricoNotificacoes(String token, String clienteCpfCnpj) throws ValidationException;

    // RF-038: Verifica se pode enviar notificação (não duplicada no mesmo dia)
    boolean podeEnviarNotificacao(String token, String clienteCpfCnpj, String hidrometroIdSha) throws ValidationException;

    // RF-039: Retorna histórico de auditoria das operações realizadas no sistema
    List<AuditEntry> obterHistoricoAuditoriaCompleto(String token) throws ValidationException;
}
