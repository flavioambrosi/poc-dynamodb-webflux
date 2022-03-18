package br.study.dynamodb.reactive.model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Parceiro {

    public static final String ENTIDADE = "Parceiro";
    private static final String PARCEIRO_PREFIX = "PARCEIRO#";

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;
    @Getter(onMethod_ = {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;
    @Getter(onMethod_ = {@DynamoDbAttribute("entidade")})
    private String entidade;
    @Getter(onMethod_ = {@DynamoDbAttribute("numeroTransacao")})
    private String numeroTransacao;
    @Getter(onMethod_ = {@DynamoDbAttribute("nome")})
    private String nome;
    @Getter(onMethod_ = {@DynamoDbAttribute("id")})
    private String id;

    @Getter(onMethod_ = {@DynamoDbAttribute("GSI1_PK")})
    private String gsi1_pk;
    @Getter(onMethod_ = {@DynamoDbAttribute("GSI1_SK")})
    private String gsi1_sk;
    @Getter(onMethod_ = {@DynamoDbAttribute("GSI2_PK")})
    private String gsi2_pk;
    @Getter(onMethod_ = {@DynamoDbAttribute("GSI2_SK")})
    private String gsi2_sk;

    public static String prefixedId(String id) {
        return PARCEIRO_PREFIX + id;
    }

    public void mountPkSk(){
        this.pk =
                Transacao.prefixedId(this.numeroTransacao);
        this.sk = prefixedId(this.id);
        this.mountGIs();
    }

    private void mountGIs(){
        this.gsi1_pk = this.sk;
        this.gsi1_sk = this.numeroTransacao;
    }
}
