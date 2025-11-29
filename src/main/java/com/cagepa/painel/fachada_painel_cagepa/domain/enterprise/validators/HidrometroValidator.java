package com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.validators;

import java.io.File;
import java.time.LocalDate;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosCadastrarHidrometroInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.application.dtos.input.DadosEnderecoInputDTO;
import com.cagepa.painel.fachada_painel_cagepa.domain.enterprise.enums.StatusHidrometro;

@Component
public class HidrometroValidator {
    
    @Autowired
    private EnderecoValidator enderecoValidator;
    
    private static final Pattern NUMERO_SERIE_PATTERN = Pattern.compile(
        "^[A-Za-z0-9-]{5,50}$"
    );
    
    private static final int MIN_LIMITE_CONSUMO = 1;
    private static final int MAX_LIMITE_CONSUMO = 9999;
    
    private static final int MIN_LIMITE_VAZAO = 1;
    private static final int MAX_LIMITE_VAZAO = 999;

    public boolean validarCadastro(DadosCadastrarHidrometroInputDTO dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados do hidrômetro não podem ser nulos.");
        }

        validarIdSha(dados.idSha());
        validarDataInstalacao(dados.dataInstalacao());
        
        if (dados.limiteConsumoMensalM3() != null) {
            validarLimiteConsumo(dados.limiteConsumoMensalM3());
        }
        
        if (dados.limiteVazaoLMin() != null) {
            validarLimiteVazao(dados.limiteVazaoLMin());
        }

        if (dados.enderecoInstalacao() == null) {
            throw new IllegalArgumentException("Endereço de instalação é obrigatório.");
        }
        
        validarEnderecoInstalacao(dados.enderecoInstalacao());

        if (dados.caminhoImagemSha() != null) {
            validarCaminhoImagemComArquivos(dados.caminhoImagemSha());
        }

        return true;
    }

    public void validarEnderecoInstalacao(DadosCadastrarHidrometroInputDTO dados) {
        if (dados.enderecoInstalacao() == null) {
            throw new IllegalArgumentException("Endereço de instalação é obrigatório.");
        }


    }

    public void validarIdSha(String idSha) {
        if (idSha == null || idSha.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do SHA é obrigatório.");
        }
        
        if (!NUMERO_SERIE_PATTERN.matcher(idSha).matches()) {
            throw new IllegalArgumentException("ID do SHA inválido. Deve conter entre 5 e 50 caracteres alfanuméricos e hífens.");
        }
    }

    public void validarDataInstalacao(LocalDate dataInstalacao) {
        if (dataInstalacao == null) {
            throw new IllegalArgumentException("Data de instalação é obrigatória.");
        }
        
        if (dataInstalacao.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de instalação não pode ser futura.");
        }
        
        if (dataInstalacao.isBefore(LocalDate.now().minusYears(50))) {
            throw new IllegalArgumentException("Data de instalação não pode ser anterior a " + LocalDate.now().minusYears(50));
        }
    }

    public void validarLimiteConsumo(Integer limiteConsumo) {
        if (limiteConsumo == null) {
            return;
        }
        
        if (limiteConsumo < MIN_LIMITE_CONSUMO || limiteConsumo > MAX_LIMITE_CONSUMO) {
            throw new IllegalArgumentException(
                String.format("Limite de consumo mensal deve estar entre %d e %d m³.", 
                    MIN_LIMITE_CONSUMO, MAX_LIMITE_CONSUMO)
            );
        }
    }

    public void validarLimiteVazao(Integer limiteVazao) {
        if (limiteVazao == null) {
            return;
        }
        
        if (limiteVazao < MIN_LIMITE_VAZAO || limiteVazao > MAX_LIMITE_VAZAO) {
            throw new IllegalArgumentException(
                String.format("Limite de vazão deve estar entre %d e %d L/min.", 
                    MIN_LIMITE_VAZAO, MAX_LIMITE_VAZAO)
            );
        }
    }

    public void validarStatus(StatusHidrometro status) {
        if (status == null) {
            throw new IllegalArgumentException("Status do hidrômetro é obrigatório.");
        }
        
        if (status == StatusHidrometro.INATIVO) {
            throw new IllegalArgumentException("Um hidrômetro não pode ser cadastrado com status INATIVO. Use ATIVO ou MANUTENCAO.");
        }
    }

    public void validarCaminhoImagem(String caminhoImagem) {
        if (caminhoImagem != null && caminhoImagem.length() > 255) {
            throw new IllegalArgumentException("Caminho da imagem não pode exceder 255 caracteres.");
        }
    }

    /**
     * Valida se o diretório de imagens existe e contém arquivos.
     */
    public void validarCaminhoImagemComArquivos(String caminhoImagem) {
        if (caminhoImagem == null || caminhoImagem.trim().isEmpty()) {
            throw new IllegalArgumentException("Caminho das imagens do SHA é obrigatório.");
        }

        if (caminhoImagem.length() > 255) {
            throw new IllegalArgumentException("Caminho da imagem não pode exceder 255 caracteres.");
        }

        File diretorio = new File(caminhoImagem);
        
        if (!diretorio.exists()) {
            throw new IllegalArgumentException(
                "O diretório de imagens do SHA não existe: " + caminhoImagem
            );
        }
        
        if (!diretorio.isDirectory()) {
            throw new IllegalArgumentException(
                "O caminho especificado não é um diretório válido: " + caminhoImagem
            );
        }
        
        File[] arquivos = diretorio.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".jpg") || 
            name.toLowerCase().endsWith(".jpeg") || 
            name.toLowerCase().endsWith(".png")
        );
        
        if (arquivos == null || arquivos.length == 0) {
            throw new IllegalArgumentException(
                "O SHA não está gerando imagens. Nenhuma imagem encontrada no diretório: " + caminhoImagem
            );
        }
    }
    
    public void validarEnderecoInstalacao(DadosEnderecoInputDTO endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço de instalação é obrigatório.");
        }
        
        boolean enderecoValido = enderecoValidator.validarEnderecoCompleto(
            endereco.logradouro(),
            endereco.numero(),
            endereco.complemento(),
            endereco.bairro(),
            endereco.cidade(),
            endereco.estado(),
            endereco.cep(),
            endereco.tipoEndereco().name()
        );
        
        if (!enderecoValido) {
            throw new IllegalArgumentException("Dados do endereço de instalação são inválidos. Verifique todos os campos.");
        }
    }
}
