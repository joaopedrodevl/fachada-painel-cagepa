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
    @Autowired
    private IHidrometroJpaRepository hidrometroJpaRepository;

    @Autowired
    private IClienteJpaRepository clienteJpaRepository;

    @Autowired
    private IEnderecoJpaRepository enderecoJpaRepository;

    @Autowired
    private EnderecoFactory enderecoFactory;

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
}
