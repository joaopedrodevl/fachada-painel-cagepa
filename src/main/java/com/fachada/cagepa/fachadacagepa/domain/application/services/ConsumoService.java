package com.fachada.cagepa.fachadacagepa.domain.application.services;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoClientePeriodoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoHidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidatorBuilder;
import com.fachada.cagepa.fachadacagepa.domain.util.CpfCnpjValidator;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IClienteJpaRepository;
import com.fachada.cagepa.fachadacagepa.infra.persistence.LeituraHidrometroRepositoryImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsumoService {
    private final IClienteJpaRepository clienteJpaRepository;
    private final LeituraHidrometroRepositoryImpl leituraHidrometroRepository;

    public ConsumoService(IClienteJpaRepository clienteJpaRepository, 
                          LeituraHidrometroRepositoryImpl leituraHidrometroRepository) {
        this.clienteJpaRepository = clienteJpaRepository;
        this.leituraHidrometroRepository = leituraHidrometroRepository;
    }

    /**
     * Obtém a última leitura de cada hidrômetro do cliente e retorna o consumo total
     *
     * @param clienteCpfCnpj CPF ou CNPJ do cliente
     * @return DTO com consumo total e por hidrometro (última leitura de cada)
     */
    @Transactional(readOnly = true)
    public ConsumoClientePeriodoDTO obterUltimaLeitura(String clienteCpfCnpj) throws ValidationException {
        // Valida CPF/CNPJ
        ValidatorBuilder.stringValidator("CPF/CNPJ do Cliente")
                .notNullOrEmpty()
                .cpfOrCnpj()
                .build()
                .validate(clienteCpfCnpj);

        // Limpa CPF/CNPJ antes de buscar
        String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(clienteCpfCnpj);

        // Buscar cliente
        var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

        // Obter última leitura de cada hidrometro
        List<ConsumoHidrometroDTO> consumoPorHidrometro = new ArrayList<>();
        int consumoTotal = 0;

        for (Hidrometro hidrometro : cliente.getHidrometros()) {
            // Buscar última leitura do hidrometro
            var ultimaLeitura = leituraHidrometroRepository.findByShaId(hidrometro.getIdSha())
                    .stream()
                    .filter(l -> l.getDataLeitura() != null)
                    .max((l1, l2) -> l1.getDataLeitura().compareTo(l2.getDataLeitura()));

            if (ultimaLeitura.isPresent()) {
                int valorLeitura = ultimaLeitura.get().getValor();
                consumoPorHidrometro.add(new ConsumoHidrometroDTO(
                        hidrometro.getIdSha(),
                        valorLeitura
                ));
                consumoTotal += valorLeitura;
            }
        }

        return new ConsumoClientePeriodoDTO(
                cliente.getCpfCnpj(),
                cliente.getNomeCompleto() != null ? cliente.getNomeCompleto() : cliente.getNomeFantasia(),
                "ATUAL",
                consumoTotal,
                consumoPorHidrometro
        );
    }

    /**
     * Calcula consumo diário de um cliente
     */
    @Transactional(readOnly = true)
    public ConsumoClientePeriodoDTO calcularConsumoDiario(String clienteCpfCnpj) throws ValidationException {
        return obterUltimaLeitura(clienteCpfCnpj);
    }

    /**
     * Calcula consumo semanal de um cliente
     */
    @Transactional(readOnly = true)
    public ConsumoClientePeriodoDTO calcularConsumoSemanal(String clienteCpfCnpj) throws ValidationException {
        return obterUltimaLeitura(clienteCpfCnpj);
    }

    /**
     * Calcula consumo mensal de um cliente
     */
    @Transactional(readOnly = true)
    public ConsumoClientePeriodoDTO calcularConsumoMensal(String clienteCpfCnpj) throws ValidationException {
        return obterUltimaLeitura(clienteCpfCnpj);
    }

    /**
     * Calcula consumo anual de um cliente
     */
    @Transactional(readOnly = true)
    public ConsumoClientePeriodoDTO calcularConsumoAnual(String clienteCpfCnpj) throws ValidationException {
        return obterUltimaLeitura(clienteCpfCnpj);
    }

    /**
     * Calcula consumo mensal de um hidrometro especifico
     */
    @Transactional(readOnly = true)
    public Double calcularConsumoMensalHidrometro(String hidrometroId, int ano, int mes) {
        try {
            return leituraHidrometroRepository.calcularConsumoMensalHidrometro(hidrometroId, ano, mes);
        } catch (Exception e) {
            return 0.0;
        }
    }
}

