package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosEnderecoInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.entities.Endereco;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.factories.interfaces.IEnderecoFactory;

public class EnderecoFactory implements IEnderecoFactory {
    @Override
    public Endereco criarEndereco(DadosEnderecoInputDTO dados) {
        return new Endereco(
            dados.logradouro(),
            dados.numero(),
            dados.complemento(),
            dados.bairro(),
            dados.cidade(),
            dados.estado(),
            dados.cep(),
            dados.tipoEndereco()
        );
    }
}
