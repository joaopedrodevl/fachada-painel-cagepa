package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.HidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.EnderecoFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto.HidrometroValidator;
import com.fachada.cagepa.fachadacagepa.domain.util.CpfCnpjValidator;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Endereco;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IClienteJpaRepository;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IEnderecoJpaRepository;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IHidrometroJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HidrometroService {
    private final IHidrometroJpaRepository hidrometroJpaRepository;

    private final IClienteJpaRepository clienteJpaRepository;

    private final IEnderecoJpaRepository enderecoJpaRepository;

    private final EnderecoFactory enderecoFactory;

    public HidrometroService(IHidrometroJpaRepository hidrometroJpaRepository, IClienteJpaRepository clienteJpaRepository, IEnderecoJpaRepository enderecoJpaRepository, EnderecoFactory enderecoFactory) {
        this.hidrometroJpaRepository = hidrometroJpaRepository;
        this.clienteJpaRepository = clienteJpaRepository;
        this.enderecoJpaRepository = enderecoJpaRepository;
        this.enderecoFactory = enderecoFactory;
    }

    @Transactional
    public void salvarHidrometro(HidrometroDTO hidrometro) throws ValidationException {
        // Valida os dados do hidrometro
        HidrometroValidator.validate(hidrometro);

        var hidrometroExists = hidrometroJpaRepository.findById(hidrometro.idSha());

        if (hidrometroExists.isPresent()) {
            throw new IllegalArgumentException("Hidrometro com ID já existe");
        }

        // Limpa CPF/CNPJ antes de buscar cliente
        String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(hidrometro.clienteCpfCnpj());

        var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

        var enderecoDTO = hidrometro.enderecoInstalacao();

        var enderecoExistente = enderecoJpaRepository.findByClienteAndLogradouroAndNumeroAndComplemento(
                cliente,
                enderecoDTO.logradouro(),
                enderecoDTO.numero(),
                enderecoDTO.complemento()
        );

        Endereco enderecoInstalacao;
        if (enderecoExistente.isPresent()) {
            enderecoInstalacao = enderecoExistente.get();
            System.out.println("Endereço já existente encontrado para o cliente, usando ID: " + enderecoInstalacao.getId());
        } else {
            enderecoInstalacao = enderecoFactory.createEndereco(enderecoDTO);
            enderecoInstalacao.setCliente(cliente);

            enderecoJpaRepository.save(enderecoInstalacao);
            System.out.println("Novo endereço criado para o cliente com ID: " + enderecoInstalacao.getId());
        }

        var h = new Hidrometro(
                hidrometro.idSha(),
                hidrometro.dataInstalacao(),
                null,
                hidrometro.status(),
                hidrometro.limiteConsumoMensalM3()
        );

        h.setCliente(cliente);
        h.setEnderecoInstalacao(enderecoInstalacao);

        hidrometroJpaRepository.save(h);
    }

    /**
     * Ativar/Desativar um hidrômetro
     * @param idSha ID SHA do hidrômetro
     * @param ativo true para ativar, false para desativar
     * @return true se status alterado com sucesso
     * @throws IllegalArgumentException se hidrômetro não encontrado
     */
    @Transactional
    public boolean alterarStatusHidrometro(String idSha, boolean ativo) throws IllegalArgumentException {
        try {
            var hidrometro = hidrometroJpaRepository.findById(idSha)
                    .orElseThrow(() -> new IllegalArgumentException("Hidrometro não encontrado com ID SHA: " + idSha));

            com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusHidrometro novoStatus =
                    ativo ? com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusHidrometro.ATIVO
                            : com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusHidrometro.INATIVO;

            hidrometro.setStatus(novoStatus);
            hidrometroJpaRepository.save(hidrometro);

            System.out.println("Hidrometro " + idSha + " status alterado para: " + novoStatus);
            return true;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao alterar status do hidrometro: " + e.getMessage(), e);
        }
    }

    /**
     * Retornar hidrômetros por cliente
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @return Lista de hidrômetros ativos do cliente
     * @throws IllegalArgumentException se cliente não encontrado
     */
    @Transactional(readOnly = true)
    public java.util.List<Hidrometro> obterHidrometrosPorCliente(String clienteCpfCnpj) throws IllegalArgumentException {
        try {
            String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(clienteCpfCnpj);

            var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

            return cliente.getHidrometros()
                    .stream()
                    .filter(h -> h.getStatus() == com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusHidrometro.ATIVO)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter hidrômetros do cliente: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar um hidrômetro pelo identificador SHA
     * @param idSha ID SHA do hidrômetro
     * @return Optional com os dados do hidrômetro
     */
    @Transactional(readOnly = true)
    public java.util.Optional<Hidrometro> obterHidrometroPorSha(String idSha) {
        try {
            // RF-022: Validar identificador SHA
            if (idSha == null || idSha.trim().isEmpty()) {
                throw new ValidationException("ID SHA não pode ser vazio");
            }

            if (idSha.length() > 50) {
                throw new ValidationException("ID SHA não pode ter mais de 50 caracteres");
            }

            return hidrometroJpaRepository.findById(idSha.trim());
        } catch (ValidationException e) {
            throw new RuntimeException("Validação de SHA falhou: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter hidrometro: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todos os hidrômetros cadastrados
     * @return Lista de todos os hidrômetros
     */
    @Transactional(readOnly = true)
    public java.util.List<Hidrometro> listarTodosHidrometros() {
        try {
            return hidrometroJpaRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar hidrometros: " + e.getMessage(), e);
        }
    }

    /**
     * Atualizar dados de um hidrômetro
     * @param idSha ID SHA do hidrômetro
     * @param novoLimite Novo limite mensal em m³
     * @return true se atualizado com sucesso
     */
    @Transactional
    public boolean atualizarLimiteConsumoHidrometro(String idSha, Integer novoLimite) throws IllegalArgumentException {
        try {
            // Validar o SHA
            if (idSha == null || idSha.trim().isEmpty() || idSha.length() > 50) {
                throw new ValidationException("ID SHA inválido");
            }

            // Validar o novo limite
            if (novoLimite == null || novoLimite <= 0) {
                throw new ValidationException("Limite de consumo deve ser maior que zero");
            }

            var hidrometro = hidrometroJpaRepository.findById(idSha)
                    .orElseThrow(() -> new IllegalArgumentException("Hidrometro não encontrado com ID SHA: " + idSha));

            hidrometro.setLimiteConsumoMensalM3(novoLimite);
            hidrometroJpaRepository.save(hidrometro);

            System.out.println("Limite do hidrômetro " + idSha + " atualizado para " + novoLimite + " m³");
            return true;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (ValidationException e) {
            throw new RuntimeException("Validação falhou: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar hidrometro: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém detalhes completos de um hidrômetro incluindo leituras
     * @param idSha ID SHA do hidrômetro
     * @return Optional com dados do hidrometro
     */
    @Transactional(readOnly = true)
    public java.util.Optional<Hidrometro> obterDetalhesHidrometro(String idSha) {
        try {
            // Validar identificador SHA
            if (idSha == null || idSha.trim().isEmpty() || idSha.length() > 50) {
                throw new ValidationException("ID SHA inválido");
            }

            return hidrometroJpaRepository.findById(idSha.trim());
        } catch (ValidationException e) {
            throw new RuntimeException("Validação de SHA falhou: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter detalhes do hidrometro: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se um hidrômetro está ativo
     * @param idSha ID SHA do hidrômetro
     * @return true se ativo, false se inativo
     */
    @Transactional(readOnly = true)
    public boolean isHidrometroAtivo(String idSha) {
        try {
            var hidrometro = hidrometroJpaRepository.findById(idSha).orElse(null);
            return hidrometro != null && hidrometro.getStatus() == com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusHidrometro.ATIVO;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar status do hidrometro: " + e.getMessage(), e);
        }
    }
}
