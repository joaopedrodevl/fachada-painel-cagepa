package com.fachada.cagepa.fachadacagepa.domain.enterprise.factories;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.EnderecoDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoEndereco;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoFactory implements IEnderecoFactory{
    @Override
    public Endereco createEndereco(EnderecoDTO enderecoDTO) {
        String cepSanitizado = sanitizarCep(enderecoDTO.cep());
        String estadoSanitizado = sanitizarEstado(enderecoDTO.estado());

        return new Endereco(
                enderecoDTO.logradouro(),
                enderecoDTO.numero(),
                enderecoDTO.complemento(),
                enderecoDTO.bairro(),
                enderecoDTO.cidade(),
                estadoSanitizado,
                cepSanitizado,
                enderecoDTO.tipoEndereco()
        );
    }

    /**
     * Método alternativo para criar um endereço
     */
    public Endereco criarEndereco(EnderecoDTO enderecoDTO) {
        return createEndereco(enderecoDTO);
    }

    /**
     * Cria um endereço com tipo padrão (RESIDENCIAL)
     */
    public Endereco criarEnderecoComTipoPadrao(String logradouro, String numero, String complemento,
                                                String bairro, String cidade, String estado, String cep) {
        return criarEndereco(new EnderecoDTO(logradouro, numero, complemento, bairro, cidade, estado, cep, TipoEndereco.RESIDENCIAL));
    }

    private String sanitizarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("Estado não pode ser vazio");
        }

        String estadoLimpo = estado.trim().toUpperCase();

        if (estadoLimpo.length() > 2) {
            throw new IllegalArgumentException("Estado deve ter no máximo 2 caracteres. Recebido: " + estadoLimpo);
        }

        return estadoLimpo;
    }

    private String sanitizarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            throw new IllegalArgumentException("CEP não pode ser vazio");
        }

        String cepLimpo = cep.replaceAll("[^0-9]", "");

        if (cepLimpo.length() > 8) {
            throw new IllegalArgumentException("CEP não pode exceder 8 dígitos. Recebido: " + cepLimpo);
        }

        if (cepLimpo.isEmpty()) {
            throw new IllegalArgumentException("CEP deve conter pelo menos um dígito");
        }

        return cepLimpo;
    }
}
