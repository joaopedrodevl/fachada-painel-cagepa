package com.fachada.cagepa.fachadacagepa.domain.enterprise.notification;

import lombok.Getter;

/**
 * Determina quando enviar notificacao baseado no percentual de consumo
 */
@Getter
public class ConsumoLimiarStrategy {

    private double limiarPercentual;

    public ConsumoLimiarStrategy(double limiarPercentual) {
        if (limiarPercentual < 0 || limiarPercentual > 100) {
            throw new IllegalArgumentException("Limiar deve estar entre 0 e 100");
        }
        this.limiarPercentual = limiarPercentual;
    }

    public boolean deveCautivar(double percentualConsumo) {
        return percentualConsumo >= limiarPercentual;
    }

    public void setLimiarPercentual(double novoLimiar) {
        if (novoLimiar < 0 || novoLimiar > 100) {
            throw new IllegalArgumentException("Limiar deve estar entre 0 e 100");
        }
        this.limiarPercentual = novoLimiar;
    }

    @Override
    public String toString() {
        return String.format("ConsumoLimiarStrategy{limiar=%.2f%%}", limiarPercentual);
    }
}

