package com.fachada.cagepa.fachadacagepa.domain.enterprise.factories;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.entity.LeituraHidrometro;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LeituraHidrometroFactory {
    public LeituraHidrometro criar(String clienteId, String valorLido) {
        return new LeituraHidrometro(clienteId, valorLido, LocalDateTime.now());
    }
}
