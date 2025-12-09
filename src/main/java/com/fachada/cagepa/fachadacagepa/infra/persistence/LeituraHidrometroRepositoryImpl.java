package com.fachada.cagepa.fachadacagepa.infra.persistence;

import com.fachada.cagepa.fachadacagepa.domain.application.repository.ILeituraHidrometroRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LeituraHidrometroRepositoryImpl implements ILeituraHidrometroRepository {

    private final ILeituraHidrometroJpaRepository jpaRepository;

    public LeituraHidrometroRepositoryImpl(ILeituraHidrometroJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<LeituraHidrometro> findByClienteId(String clienteId) {
        return jpaRepository.findAll().stream()
                .filter(l -> l.getClienteId().equals(clienteId))
                .toList();
    }

    @Override
    public LeituraHidrometro save(LeituraHidrometro entity) {
        return (LeituraHidrometro) jpaRepository.save(entity);
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
}
