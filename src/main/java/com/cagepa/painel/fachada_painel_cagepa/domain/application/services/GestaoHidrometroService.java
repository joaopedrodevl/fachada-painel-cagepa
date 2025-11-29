package com.cagepa.painel.fachada_painel_cagepa.domain.application.services;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosAtualizarHidrometroInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastrarHidrometroInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories.IClienteRepository;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories.IEnderecoRepository;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.repositories.IHidrometroRepository;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Cliente;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Endereco;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Hidrometro;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.StatusHidrometro;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories.EnderecoFactory;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.validators.ClienteValidator;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.validators.HidrometroValidator;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.valueObjects.CpfCnpj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GestaoHidrometroService {
    @Autowired
    private IHidrometroRepository hidrometroRepository;

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IEnderecoRepository enderecoRepository;

    @Autowired
    private ClienteValidator clienteValidator;

    @Autowired
    private HidrometroValidator hidrometroValidator;

    @Autowired
    private EnderecoFactory enderecoFactory;

    @Transactional
    public Hidrometro vincular(String cpfCnpjCliente, DadosCadastrarHidrometroInputDTO dados) {
        // Validar dados do hidrômetro
        hidrometroValidator.validarCadastro(dados);
        
        // Validar o cpfCnpj
        clienteValidator.validarCpfCnpj(cpfCnpjCliente);

        // Buscar o cliente
        Cliente cliente = clienteRepository.findByCpfCnpjValor(CpfCnpj.create(cpfCnpjCliente).getValor())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o CPF/CNPJ informado."));

        // Verificar se o hidrômetro já existe
        hidrometroRepository.findById(dados.idSha()).ifPresent(h -> {
            throw new IllegalArgumentException("Já existe um hidrômetro cadastrado com o ID do SHA informado.");
        });

        // Criar e salvar o endereço de instalação
        Endereco enderecoInstalacao = enderecoFactory.criarEndereco(dados.enderecoInstalacao());
        enderecoInstalacao = enderecoRepository.salvar(enderecoInstalacao);

        // Criar o hidrômetro
        Hidrometro hidrometro = new Hidrometro();
        hidrometro.setIdSha(dados.idSha());
        hidrometro.setEnderecoInstalacao(enderecoInstalacao);
        hidrometro.setDataInstalacao(dados.dataInstalacao());
        hidrometro.setLimiteConsumoMensalM3(dados.limiteConsumoMensalM3());
        hidrometro.setLimiteVazaoLMin(dados.limiteVazaoLMin());
        hidrometro.setCaminhoImagemSHA(dados.caminhoImagemSha());
        hidrometro.setCliente(cliente);

        return hidrometroRepository.salvar(hidrometro);
    }

    /**
     * Lista todos os hidrômetros vinculados a um cliente.
     */
    public List<Hidrometro> listarPorCliente(String cpfCnpjCliente) {
        clienteValidator.validarCpfCnpj(cpfCnpjCliente);

        Cliente cliente = clienteRepository.findByCpfCnpjValor(CpfCnpj.create(cpfCnpjCliente).getValor())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o CPF/CNPJ informado."));

        return hidrometroRepository.findByClienteId(cliente.getId());
    }

    /**
     * Desvincula um hidrômetro de um cliente, marcando-o como DESATIVADO.
     */
    @Transactional
    public boolean desvincular(String idSha) {
        Hidrometro hidrometro = hidrometroRepository.findById(idSha)
                .orElseThrow(() -> new IllegalArgumentException("Hidrômetro não encontrado com o ID do SHA informado."));

        hidrometro.setStatus(StatusHidrometro.DESATIVADO);
        hidrometro.setDataRemocao(LocalDate.now());
        
        hidrometroRepository.salvar(hidrometro);
        return true;
    }

    /**
     * Consulta um hidrômetro pelo ID do SHA.
     */
    public Hidrometro consultar(String idSha) {
        return hidrometroRepository.findById(idSha)
                .orElseThrow(() -> new IllegalArgumentException("Hidrômetro não encontrado com o ID do SHA informado."));
    }

    /**
     * Atualiza informações de um hidrômetro.
     */
    @Transactional
    public Hidrometro atualizar(String idSha, DadosAtualizarHidrometroInputDTO dados) {
        Hidrometro hidrometro = consultar(idSha);
        hidrometro.atualizarInformacoes(dados);
        return hidrometroRepository.salvar(hidrometro);
    }
}
