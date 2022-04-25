package br.study.dynamodb.reactive.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransacaoResponse {

    private String numeroTransacao;
    private String moeda;
    private BigDecimal valor;
}
