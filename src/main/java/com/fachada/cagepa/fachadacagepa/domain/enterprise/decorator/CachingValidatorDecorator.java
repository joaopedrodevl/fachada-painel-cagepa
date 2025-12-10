package com.fachada.cagepa.fachadacagepa.domain.enterprise.decorator;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;
import java.util.HashMap;
import java.util.Map;

/**
 * Cacheia resultados de validações para melhorar performance
 * Útil quando validações complexas são chamadas repetidamente
 */
public class CachingValidatorDecorator<T> extends ValidatorDecorator<T> {
    
    private final Map<String, Boolean> cache = new HashMap<>();
    
    public CachingValidatorDecorator(Validator<T> validator) {
        super(validator);
    }
    
    @Override
    public boolean isValid(T value) {
        String key = value != null ? value.toString() : "null";
        
        // Se está em cache, retorna o resultado cached
        if (cache.containsKey(key)) {
            System.out.println("Usando resultado em cache para: " + key);
            return cache.get(key);
        }
        
        // Caso contrário, valida e cacheia o resultado
        boolean result = super.isValid(value);
        cache.put(key, result);
        System.out.println("Resultado cacheado para: " + key);
        return result;
    }
    
    /**
     * Limpa o cache
     */
    public void clearCache() {
        cache.clear();
        System.out.println("Cache limpo");
    }
    
    /**
     * Retorna tamanho do cache
     */
    public int getCacheSize() {
        return cache.size();
    }
}


