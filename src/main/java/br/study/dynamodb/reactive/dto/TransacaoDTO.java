package br.study.dynamodb.reactive.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransacaoDTO {

    private String numeroTransacao;
    private BigDecimal valor;
    private String moeda;
    private String data;
    private String situacao;
    private ParceiroDTO parceiro;
    private OperacaoBandeiraDTO pull;
    private List<OperacaoBandeiraDTO> push = new ArrayList<>();
    private List<OperacaoBandeiraDTO> reversals = new ArrayList<>();

    public OperacaoBandeiraDTO getOperacaoPushPorId(String id){
        List<OperacaoBandeiraDTO> result = push.stream().filter(op -> op.getId().equals(id))
                .collect(Collectors.toList());
        if(!result.isEmpty()){
            return result.get(0);
        }
        return null;
    }

    public void addRecebedorOperacaoPushPorId(String id, PessoaDTO recebedor) {
        OperacaoBandeiraDTO operacaoBandeiraDTO = push.stream().filter(op -> op.getId().equals(id))
                .collect(Collectors.toList()).stream().findFirst()
                .orElse(null);

        if(operacaoBandeiraDTO == null){
            operacaoBandeiraDTO = OperacaoBandeiraDTO.builder().id(id).build();
            push.add(operacaoBandeiraDTO);
        }
        operacaoBandeiraDTO.setDadosPortador(recebedor);
    }

    public void addDadosbandeiraOperacaoPush(String id, TransacaoBandeiraDTO transacaoBandeiraDTO) {
        OperacaoBandeiraDTO operacaoBandeiraDTO = push.stream().filter(op -> op.getId().equals(id))
                .collect(Collectors.toList()).stream().findFirst()
                .orElse(null);

        if(operacaoBandeiraDTO == null){
            operacaoBandeiraDTO = OperacaoBandeiraDTO.builder().id(id).build();
            push.add(operacaoBandeiraDTO);
        }
        operacaoBandeiraDTO.setDadosBandeira(transacaoBandeiraDTO);
    }

}
