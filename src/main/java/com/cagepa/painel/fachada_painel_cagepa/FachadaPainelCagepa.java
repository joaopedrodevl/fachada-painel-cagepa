package com.cagepa.painel.fachada_painel_cagepa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosAtualizarClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosAtualizarHidrometroInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastrarHidrometroInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastroClienteInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosEnderecoInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.services.GestaoClientesService;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.services.GestaoHidrometroService;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Cliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Hidrometro;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoCliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.TipoEndereco;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.CpfCnpj;

import java.time.LocalDate;
import java.util.List;

@Component
public class FachadaPainelCagepa {

    private final GestaoHidrometroService gestaoHidrometroService;
    @Autowired
    private GestaoClientesService gestaoClientesService;

    private FachadaPainelCagepa(GestaoHidrometroService gestaoHidrometroService) {
        this.gestaoHidrometroService = gestaoHidrometroService;
    }

    void inicializarSistema() {
        System.out.println("Sistema inicializado com sucesso!");
    }   

    /**
     * Cadastra um cliente Pessoa Física (CPF).
     * Usado para clientes residenciais e comerciais pequenos.
     */
    Cliente cadastrarClientePessoaFisica(
        String cpf, 
        String nomeCompleto, 
        String email, 
        String telefone, 
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        String tipoCliente
    ) {
        DadosCadastroClienteInputDTO dadosCliente = new DadosCadastroClienteInputDTO(
            CpfCnpj.create(cpf),
            nomeCompleto,
            null, 
            email,
            telefone,
            new DadosEnderecoInputDTO(logradouro, numero, complemento, bairro, cidade, estado, cep, TipoEndereco.valueOf(tipoCliente.toUpperCase())),
            TipoCliente.valueOf(tipoCliente.toUpperCase())
        );

        Cliente cliente = gestaoClientesService.cadastrar(dadosCliente);
        System.out.println("Cliente Pessoa Física cadastrado com sucesso: " + cliente.getNomeExibicao());
        return cliente;
    }

    /**
     * Cadastra um cliente Pessoa Jurídica (CNPJ).
     * Usado para clientes comerciais e industriais.
     */
    Cliente cadastrarClientePessoaJuridica(
        String cnpj, 
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
        String tipoCliente
    ) {
        DadosCadastroClienteInputDTO dadosCliente = new DadosCadastroClienteInputDTO(
            CpfCnpj.create(cnpj),
            nomeFantasia,
            razaoSocial,
            email,
            telefone,
            new DadosEnderecoInputDTO(logradouro, numero, complemento, bairro, cidade, estado, cep, TipoEndereco.valueOf(tipoCliente.toUpperCase())),
            TipoCliente.valueOf(tipoCliente.toUpperCase())
        );

        Cliente cliente = gestaoClientesService.cadastrar(dadosCliente);
        System.out.println("Cliente Pessoa Jurídica cadastrado com sucesso: " + cliente.getNomeExibicao());
        return cliente;
    }

    Cliente consultarCliente(String cpfCnpj) {
        return gestaoClientesService.consultar(cpfCnpj);
    }

    /**
     * Atualiza dados cadastrais de um cliente.
     */
    Cliente atualizarCliente(String cpfCnpj, DadosAtualizarClienteInputDTO dados) {
        Cliente cliente = gestaoClientesService.atualizar(cpfCnpj, dados);
        System.out.println("Cliente atualizado com sucesso: " + cliente.getNomeExibicao());
        return cliente;
    }

    /**
     * Desativa logicamente um cliente e todos os seus hidrômetros.
     */
    boolean desativarCliente(String cpfCnpj) {
        boolean sucesso = gestaoClientesService.desativar(cpfCnpj);
        if (sucesso) {
            System.out.println("Cliente desativado com sucesso (incluindo todos os hidrômetros vinculados).");
        }
        return sucesso;
    }

    List<Cliente> listar(String filtro) {
        return gestaoClientesService.listar(filtro);
    }

    /**
     * Vincula um hidrômetro (SHA) a um cliente.
     */
    Hidrometro vincularHidrometro(
        String cpfCnpj,
        String idSha,
        String caminhoImagemSha,
        LocalDate dataInstalacao,
        Integer limiteConsumoMensalM3,
        Integer limiteVazaoLMin,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,
        String tipoEndereco
    ) {
        Hidrometro hidrometro = gestaoHidrometroService.vincular(
            cpfCnpj,
            new DadosCadastrarHidrometroInputDTO(
                idSha,
                caminhoImagemSha,
                dataInstalacao,
                limiteConsumoMensalM3,
                limiteVazaoLMin,
                new DadosEnderecoInputDTO(
                    logradouro,
                    numero,
                    complemento,
                    bairro,
                    cidade,
                    estado,
                    cep,
                    TipoEndereco.valueOf(tipoEndereco.toUpperCase())
                )
            )
        );
        System.out.println("Hidrômetro (SHA) vinculado com sucesso: " + idSha);
        return hidrometro;
    }

    /**
     * Lista todos os hidrômetros vinculados a um cliente.
     */
    List<Hidrometro> listarHidrometrosCliente(String cpfCnpj) {
        return gestaoHidrometroService.listarPorCliente(cpfCnpj);
    }

    /**
     * Consulta um hidrômetro específico pelo ID do SHA.
     */
    Hidrometro consultarHidrometro(String idSha) {
        return gestaoHidrometroService.consultar(idSha);
    }

    /**
     * Atualiza informações de um hidrômetro.
     */
    Hidrometro atualizarHidrometro(String idSha, DadosAtualizarHidrometroInputDTO dados) {
        Hidrometro hidrometro = gestaoHidrometroService.atualizar(idSha, dados);
        System.out.println("Hidrômetro atualizado com sucesso: " + idSha);
        return hidrometro;
    }

    /**
     * Desvincula um hidrômetro de um cliente, marcando-o como DESATIVADO.
     */
    boolean desvincularHidrometro(String idSha) {
        boolean sucesso = gestaoHidrometroService.desvincular(idSha);
        if (sucesso) {
            System.out.println("Hidrômetro desvinculado com sucesso: " + idSha);
        }
        return sucesso;
    }
}
