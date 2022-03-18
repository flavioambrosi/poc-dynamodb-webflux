package br.study.dynamodb.reactive.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacaoBandeiraDTO {

    @JsonIgnore
    private String id;
    private PessoaDTO dadosPortador;
    private TransacaoBandeiraDTO dadosBandeira;
}
