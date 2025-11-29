package com.cagepa.painel.fachada_painel_cagepa.domain.application.exceptions;

public class ImagemInvalidaException extends ProcessamentoException {
    public ImagemInvalidaException() {
        super("Imagem inválida fornecida.");
    }
    
}
