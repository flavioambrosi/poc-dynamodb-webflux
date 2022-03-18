package br.study.dynamodb.reactive.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FindTransacaoParams {
    private List<String> idsTransacaoBandeira;
    private List<String> emissores;
}
