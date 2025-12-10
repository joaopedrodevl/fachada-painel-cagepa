package com.fachada.cagepa.fachadacagepa.infra.persistence;

import com.fachada.cagepa.fachadacagepa.domain.application.repository.ILeituraHidrometroRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class LeituraHidrometroRepositoryImpl implements ILeituraHidrometroRepository {

    private final ILeituraHidrometroJpaRepository jpaRepository;

    public LeituraHidrometroRepositoryImpl(ILeituraHidrometroJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<LeituraHidrometro> findByShaId(String idSha) {
        return jpaRepository.findAll().stream()
                .filter(l -> l.getHidrometro().getIdSha().equals(idSha))
                .toList();
    }

    @Override
    public LeituraHidrometro save(LeituraHidrometro entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public Optional<LeituraHidrometro> findById(Long aLong) {
        return jpaRepository.findById(aLong);
    }

    @Override
    public List<LeituraHidrometro> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long aLong) {
        jpaRepository.deleteById(aLong);
    }

    /**
     * Calcula a soma de consumo (valor) para um hidrometro em um período específico
     *
     * @param idHidrometro ID do hidrometro
     * @param dataInicio Data/hora inicial do período
     * @param dataFim Data/hora final do período
     * @return Soma do consumo em m³
     */
    public int sumConsumoByHidrometroAndPeriodo(String idHidrometro, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return jpaRepository.findAll().stream()
                .filter(leitura -> leitura.getHidrometro().getIdSha().equals(idHidrometro))
                .filter(leitura -> leitura.getDataLeitura() != null &&
                        !leitura.getDataLeitura().isBefore(dataInicio) &&
                        !leitura.getDataLeitura().isAfter(dataFim))
                .mapToInt(LeituraHidrometro::getValor)
                .sum();
    }

    @Override
    public Double calcularConsumoMensalHidrometro(String idHidrometro, int ano, int mes) {
        LocalDateTime dataInicio = LocalDateTime.of(ano, mes, 1, 0, 0);
        LocalDateTime dataFim = dataInicio.plusMonths(1).minusSeconds(1);

        LeituraHidrometro ultimaLeituraDoMes = jpaRepository.findAll().stream()
                .filter(leitura -> leitura.getHidrometro().getIdSha().equals(idHidrometro))
                .filter(leitura -> leitura.getDataLeitura() != null &&
                        !leitura.getDataLeitura().isBefore(dataInicio) &&
                        !leitura.getDataLeitura().isAfter(dataFim))
                .max((l1, l2) -> l1.getDataLeitura().compareTo(l2.getDataLeitura()))
                .orElse(null);

        if (ultimaLeituraDoMes == null) {
            return 0.0;
        }

        return (double) ultimaLeituraDoMes.getValor();
    }

    /**
     * Obtém o valor da última leitura registrada para um hidrometro
     *
     * @param idHidrometro ID do hidrometro
     * @return Optional contendo o valor da última leitura, ou vazio se não houver registros
     */
    public Optional<Integer> pegarUltimaLeitura(String idHidrometro) {
        LeituraHidrometro ultimaLeitura = jpaRepository.findTopByHidrometroIdShaOrderByDataLeituraDesc(idHidrometro);
        return ultimaLeitura != null ? Optional.of(ultimaLeitura.getValor()) : Optional.empty();
    }
}
