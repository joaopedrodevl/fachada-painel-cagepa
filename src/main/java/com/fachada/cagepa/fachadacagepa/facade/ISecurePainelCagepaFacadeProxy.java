package com.fachada.cagepa.fachadacagepa.facade;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoClientePeriodoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.EnderecoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.HidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoCliente;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoEndereco;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;

import java.io.IOException;
import java.time.LocalDate;

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

    String obterHistoricoAuditoria(String token);
    boolean limparHistoricoAuditoria(String token);

    String exibirConfiguracaoAtual(String token);
    boolean configurarDiretorioImagens(String token, String novoDir);
    boolean configurarCaminhoTesseract(String token, String novoPath);

    void verificarENotificarConsumoAlto(String token);
    String obterRelatorioNotificacoes(String token);
    boolean alterarLimiarNotificacao(String token, double novoLimiar);
}
