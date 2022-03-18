package br.study.dynamodb.reactive.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PessoaDTO {

    private String emissor;
    private String bandeira;
    private String cartaoTokenizado;
    private String nome;
}
