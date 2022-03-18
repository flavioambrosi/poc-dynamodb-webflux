package br.study.dynamodb.reactive.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParceiroDTO {

    private String id;
    private String nome;
}
