package br.study.dynamodb.reactive.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
@DynamoDBTable(tableName = "core")
public class Transacao {

    private static final String TRANSACAO_PREFIX = "TRXID#";
    public static final String ENTIDADE = "Transacao";

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;
    @Getter(onMethod_ = {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;
    @Getter(onMethod_ = {@DynamoDbAttribute("valor")})
    private BigDecimal valor;
    @Getter(onMethod_ = {@DynamoDbAttribute("moeda")})
    private String moeda;
    @Getter(onMethod_ = {@DynamoDbAttribute("numeroTransacao")})
    private String numeroTransacao;
    @Getter(onMethod_ = {@DynamoDbAttribute("data")})
    private String data;
    @Getter(onMethod_ = {@DynamoDbAttribute("situacao")})
    private String situacao;
    @Getter(onMethod_ = {@DynamoDbAttribute("entidade")})
    private String entidade;

    public static String prefixedId(String id) {
        return TRANSACAO_PREFIX + id;
    }

    public void mountPkSk(){
        this.pk = prefixedId(this.numeroTransacao);
        this.sk = prefixedId(this.numeroTransacao);
    }
}
