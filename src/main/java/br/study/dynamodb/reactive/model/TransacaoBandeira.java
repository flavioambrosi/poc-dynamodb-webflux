package br.study.dynamodb.reactive.model;

import com.amazonaws.util.StringUtils;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransacaoBandeira {

    private static final String TRANSACAO_PREFIX = "BID#";
    public static final String ENTIDADE = "TransacaoBandeira";

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;
    @Getter(onMethod_ = {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;
    @Getter(onMethod_ = {@DynamoDbAttribute("operacao")})
    private String operacao;
    @Getter(onMethod_ = {@DynamoDbAttribute("idTransacaoBandeira")})
    private String idTransacaoBandeira;
    @Getter(onMethod_ = {@DynamoDbAttribute("codigoAprovacao")})
    private String codigoAprovacao;
    @Getter(onMethod_ = {@DynamoDbAttribute("emissor")})
    private String emissor;
    @Getter(onMethod_ = {@DynamoDbAttribute("operacaoContabilizada")})
    private Boolean operacaoContabilizada;
    @Getter(onMethod_ = {@DynamoDbAttribute("numeroTransacao")})
    private String numeroTransacao;
    @Getter(onMethod_ = {@DynamoDbAttribute("entidade")})
    private String entidade;
    @Getter(onMethod_ = {@DynamoDbAttribute("id")})
    private String id;
    @Getter(onMethod_ = {@DynamoDbAttribute("data")})
    private String data;

    @Getter(onMethod_ = {@DynamoDbAttribute("GSI1_PK")})
    private String gsi1_pk;

    @Getter(onMethod_ = {@DynamoDbAttribute("GSI1_SK")})
    private String gsi1_sk;

    @Getter(onMethod_ = {@DynamoDbAttribute("GSI2_PK")})
    private String gsi2_pk;

    @Getter(onMethod_ = {@DynamoDbAttribute("GSI2_SK")})
    private String gsi2_sk;

    public static String prefixedId(String idBandeira) {
        return TRANSACAO_PREFIX + idBandeira;
    }

    public void mountPkSk(){
        if(StringUtils.isNullOrEmpty(this.idTransacaoBandeira)){
            this.pk = Transacao.prefixedId(this.numeroTransacao);
            this.sk = "OP#ERRO#" + id;
        } else {
            this.pk = Transacao.prefixedId(this.numeroTransacao);
            this.sk = prefixedId(this.idTransacaoBandeira);
        }

        this.mountGIs();
    }

    private void mountGIs(){
        this.gsi1_pk = this.sk;
        this.gsi1_sk = this.emissor;

        this.gsi2_pk = this.emissor;
        this.gsi2_sk = this.sk;
    }
}
