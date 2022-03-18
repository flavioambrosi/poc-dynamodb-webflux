package br.study.dynamodb.reactive.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TransacaoBandeiraDTO {

    private String idTransacaoBandeira;
    private String codigoAprovacao;
    private String emissor;
    private Boolean operacaoContabilizada;
    private String data;
}
