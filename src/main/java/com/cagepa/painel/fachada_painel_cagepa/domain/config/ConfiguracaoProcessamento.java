package com.cagepa.painel.fachada_painel_cagepa.domain.config;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ConfiguracaoProcessamento {
    private String diretorioImagensSHA = "/var/painel_cagepa/imagens_sha/";
    private String diretorioImagensProcessadas = "/var/painel_cagepa/imagens_processadas/";
    private String diretorioImagensErro = "/var/painel_cagepa/imagens_erro/";
    private final int intervaloProcessamentoMinutos = 5;
}
