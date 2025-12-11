package com.fachada.cagepa.fachadacagepa.domain.application.services;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoClientePeriodoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoHidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidatorBuilder;
import com.fachada.cagepa.fachadacagepa.domain.util.CpfCnpjValidator;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IClienteJpaRepository;
import com.fachada.cagepa.fachadacagepa.infra.persistence.LeituraHidrometro;
import com.fachada.cagepa.fachadacagepa.infra.persistence.LeituraHidrometroRepositoryImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
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
                    .max(Comparator.comparing(LeituraHidrometro::getDataLeitura));

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
        ValidatorBuilder.stringValidator("CPF/CNPJ do Cliente")
                .notNullOrEmpty()
                .cpfOrCnpj()
                .build()
                .validate(clienteCpfCnpj);

        String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(clienteCpfCnpj);
        var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

        LocalDate hoje = LocalDate.now();
        List<ConsumoHidrometroDTO> consumoPorHidrometro = new ArrayList<>();
        int consumoTotal = 0;

        for (Hidrometro hidrometro : cliente.getHidrometros()) {
            var leiturasDia = leituraHidrometroRepository.findByShaId(hidrometro.getIdSha())
                    .stream()
                    .filter(l -> l.getDataLeitura() != null && l.getDataLeitura().toLocalDate().isEqual(hoje))
                    .toList();

            if (!leiturasDia.isEmpty()) {
                int consumoDia = leiturasDia.stream().mapToInt(LeituraHidrometro::getValor).sum();
                consumoPorHidrometro.add(new ConsumoHidrometroDTO(hidrometro.getIdSha(), consumoDia));
                consumoTotal += consumoDia;
            }
        }

        return new ConsumoClientePeriodoDTO(
                cliente.getCpfCnpj(),
                cliente.getNomeCompleto() != null ? cliente.getNomeCompleto() : cliente.getNomeFantasia(),
                "DIÁRIO",
                consumoTotal,
                consumoPorHidrometro
        );
    }

    /**
     * Calcula consumo semanal de um cliente
     */
    @Transactional(readOnly = true)
    public ConsumoClientePeriodoDTO calcularConsumoSemanal(String clienteCpfCnpj) throws ValidationException {
        ValidatorBuilder.stringValidator("CPF/CNPJ do Cliente")
                .notNullOrEmpty()
                .cpfOrCnpj()
                .build()
                .validate(clienteCpfCnpj);

        String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(clienteCpfCnpj);
        var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.minusDays(7);
        List<ConsumoHidrometroDTO> consumoPorHidrometro = new ArrayList<>();
        int consumoTotal = 0;

        for (Hidrometro hidrometro : cliente.getHidrometros()) {
            var leiturasSemana = leituraHidrometroRepository.findByShaId(hidrometro.getIdSha())
                    .stream()
                    .filter(l -> l.getDataLeitura() != null &&
                            l.getDataLeitura().toLocalDate().isAfter(inicioSemana) &&
                            l.getDataLeitura().toLocalDate().isBefore(hoje.plusDays(1)))
                    .toList();

            if (!leiturasSemana.isEmpty()) {
                int consumoSemana = leiturasSemana.stream().mapToInt(LeituraHidrometro::getValor).sum();
                consumoPorHidrometro.add(new ConsumoHidrometroDTO(hidrometro.getIdSha(), consumoSemana));
                consumoTotal += consumoSemana;
            }
        }

        return new ConsumoClientePeriodoDTO(
                cliente.getCpfCnpj(),
                cliente.getNomeCompleto() != null ? cliente.getNomeCompleto() : cliente.getNomeFantasia(),
                "SEMANAL",
                consumoTotal,
                consumoPorHidrometro
        );
    }

    /**
     * Calcula consumo mensal de um cliente
     */
    @Transactional(readOnly = true)
    public ConsumoClientePeriodoDTO calcularConsumoMensal(String clienteCpfCnpj) throws ValidationException {
        ValidatorBuilder.stringValidator("CPF/CNPJ do Cliente")
                .notNullOrEmpty()
                .cpfOrCnpj()
                .build()
                .validate(clienteCpfCnpj);

        String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(clienteCpfCnpj);
        var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

        YearMonth mesAtual = YearMonth.now();
        List<ConsumoHidrometroDTO> consumoPorHidrometro = new ArrayList<>();
        int consumoTotal = 0;

        for (Hidrometro hidrometro : cliente.getHidrometros()) {
            var leiturasMes = leituraHidrometroRepository.findByShaId(hidrometro.getIdSha())
                    .stream()
                    .filter(l -> l.getDataLeitura() != null &&
                            YearMonth.from(l.getDataLeitura()).equals(mesAtual))
                    .toList();

            if (!leiturasMes.isEmpty()) {
                int consumoMes = leiturasMes.stream().mapToInt(LeituraHidrometro::getValor).sum();
                consumoPorHidrometro.add(new ConsumoHidrometroDTO(hidrometro.getIdSha(), consumoMes));
                consumoTotal += consumoMes;
            }
        }

        return new ConsumoClientePeriodoDTO(
                cliente.getCpfCnpj(),
                cliente.getNomeCompleto() != null ? cliente.getNomeCompleto() : cliente.getNomeFantasia(),
                "MENSAL",
                consumoTotal,
                consumoPorHidrometro
        );
    }

    /**
     * Calcula consumo anual de um cliente
     */
    @Transactional(readOnly = true)
    public ConsumoClientePeriodoDTO calcularConsumoAnual(String clienteCpfCnpj) throws ValidationException {
        ValidatorBuilder.stringValidator("CPF/CNPJ do Cliente")
                .notNullOrEmpty()
                .cpfOrCnpj()
                .build()
                .validate(clienteCpfCnpj);

        String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(clienteCpfCnpj);
        var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

        int anoAtual = LocalDate.now().getYear();
        List<ConsumoHidrometroDTO> consumoPorHidrometro = new ArrayList<>();
        int consumoTotal = 0;

        for (Hidrometro hidrometro : cliente.getHidrometros()) {
            var leituraAnual = leituraHidrometroRepository.findByShaId(hidrometro.getIdSha())
                    .stream()
                    .filter(l -> l.getDataLeitura() != null &&
                            l.getDataLeitura().getYear() == anoAtual)
                    .toList();

            if (!leituraAnual.isEmpty()) {
                int consumoAnual = leituraAnual.stream().mapToInt(LeituraHidrometro::getValor).sum();
                consumoPorHidrometro.add(new ConsumoHidrometroDTO(hidrometro.getIdSha(), consumoAnual));
                consumoTotal += consumoAnual;
            }
        }

        return new ConsumoClientePeriodoDTO(
                cliente.getCpfCnpj(),
                cliente.getNomeCompleto() != null ? cliente.getNomeCompleto() : cliente.getNomeFantasia(),
                "ANUAL",
                consumoTotal,
                consumoPorHidrometro
        );
    }

    /**
     * Retorna o consumo individual de cada hidrômetro do cliente
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @return Lista com consumo individual de cada hidrometro
     */
    @Transactional(readOnly = true)
    public List<ConsumoHidrometroDTO> obterConsumoIndividualPorHidrometro(String clienteCpfCnpj) throws ValidationException {
        ValidatorBuilder.stringValidator("CPF/CNPJ do Cliente")
                .notNullOrEmpty()
                .cpfOrCnpj()
                .build()
                .validate(clienteCpfCnpj);

        String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(clienteCpfCnpj);
        var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

        List<ConsumoHidrometroDTO> consumosPorHidrometro = new ArrayList<>();

        for (Hidrometro hidrometro : cliente.getHidrometros()) {
            var ultimaLeitura = leituraHidrometroRepository.findByShaId(hidrometro.getIdSha())
                    .stream()
                    .filter(l -> l.getDataLeitura() != null)
                    .max(Comparator.comparing(LeituraHidrometro::getDataLeitura));

            ultimaLeitura.ifPresent(leituraHidrometro -> consumosPorHidrometro.add(new ConsumoHidrometroDTO(
                    hidrometro.getIdSha(),
                    leituraHidrometro.getValor()
            )));
        }

        return consumosPorHidrometro;
    }

    /**
     * Somar o consumo de todos os hidrômetros do cliente
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @return Consumo total de todos os hidrometros
     */
    @Transactional(readOnly = true)
    public int obterConsumoTotalCliente(String clienteCpfCnpj) throws ValidationException {
        ValidatorBuilder.stringValidator("CPF/CNPJ do Cliente")
                .notNullOrEmpty()
                .cpfOrCnpj()
                .build()
                .validate(clienteCpfCnpj);

        String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(clienteCpfCnpj);
        var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

        int consumoTotal = 0;

        for (Hidrometro hidrometro : cliente.getHidrometros()) {
            var ultimaLeitura = leituraHidrometroRepository.findByShaId(hidrometro.getIdSha())
                    .stream()
                    .filter(l -> l.getDataLeitura() != null)
                    .max(Comparator.comparing(LeituraHidrometro::getDataLeitura));

            if (ultimaLeitura.isPresent()) {
                consumoTotal += ultimaLeitura.get().getValor();
            }
        }

        return consumoTotal;
    }

    /**
     * Verifica se o consumo mensal por hidrômetro do cliente é >= 70% do limite cadastrado
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @return Mapa com informações de hidrometros que atingiram 70% do limite
     */
    @Transactional(readOnly = true)
    public List<ConsumoHidrometroDTO> verificarLimiteConsumo70Porcento(String clienteCpfCnpj) throws ValidationException {
        ValidatorBuilder.stringValidator("CPF/CNPJ do Cliente")
                .notNullOrEmpty()
                .cpfOrCnpj()
                .build()
                .validate(clienteCpfCnpj);

        String cpfCnpjLimpo = CpfCnpjValidator.cleanCpfCnpj(clienteCpfCnpj);
        var cliente = clienteJpaRepository.findByCpfCnpj(cpfCnpjLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF/CNPJ: " + cpfCnpjLimpo));

        List<ConsumoHidrometroDTO> hidrometrosAcimaLimite = new ArrayList<>();
        YearMonth mesAtual = YearMonth.now();

        for (Hidrometro hidrometro : cliente.getHidrometros()) {
            if (hidrometro.getLimiteConsumoMensalM3() == null) {
                continue; // Pula hidrometros sem limite definido
            }

            var leiturasMes = leituraHidrometroRepository.findByShaId(hidrometro.getIdSha())
                    .stream()
                    .filter(l -> l.getDataLeitura() != null &&
                            YearMonth.from(l.getDataLeitura()).equals(mesAtual))
                    .toList();

            if (!leiturasMes.isEmpty()) {
                int consumoMes = leiturasMes.stream().mapToInt(LeituraHidrometro::getValor).sum();
                double percentualLimite = (double) consumoMes / hidrometro.getLimiteConsumoMensalM3() * 100;

                if (percentualLimite >= 70.0) {
                    hidrometrosAcimaLimite.add(new ConsumoHidrometroDTO(
                            hidrometro.getIdSha(),
                            consumoMes
                    ));
                }
            }
        }

        return hidrometrosAcimaLimite;
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

