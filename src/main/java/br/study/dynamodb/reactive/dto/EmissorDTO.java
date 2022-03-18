package br.study.dynamodb.reactive.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmissorDTO {
    private String nomeEmissor;
    private int total;
    private List<TransacaoDTO> transacoes;
}
