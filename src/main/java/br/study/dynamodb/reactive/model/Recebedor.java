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
public class Recebedor {

    public static final String ENTIDADE = "Recebedor";
    private static final String RECEBEDOR_PREFIX = "RECEBEDOR#";

    @Getter(onMethod_ = {@DynamoDbPartitionKey, @DynamoDbAttribute("PK")})
    private String pk;
    @Getter(onMethod_ = {@DynamoDbSortKey, @DynamoDbAttribute("SK")})
    private String sk;
    @Getter(onMethod_ = {@DynamoDbAttribute("entidade")})
    private String entidade;
    @Getter(onMethod_ = {@DynamoDbAttribute("emissor")})
    private String emissor;
    @Getter(onMethod_ = {@DynamoDbAttribute("bandeira")})
    private String bandeira;
    @Getter(onMethod_ = {@DynamoDbAttribute("cartaoTokenizado")})
    private String cartaoTokenizado;
    @Getter(onMethod_ = {@DynamoDbAttribute("nome")})
    private String nome;
    //    @Getter(onMethod_ = {@DynamoDbAttribute("endereco")})
//    private Map endereco;
    @Getter(onMethod_ = {@DynamoDbAttribute("numeroTransacao")})
    private String numeroTransacao;
    @Getter(onMethod_ = {@DynamoDbAttribute("id")})
    private String id;

    public static String prefixedId(String cartaoTokenizado) {
        return RECEBEDOR_PREFIX + cartaoTokenizado;
    }

    public void mountPkSk(){
        this.pk = Transacao.prefixedId(this.numeroTransacao);
        this.setSk(prefixedId(this.getCartaoTokenizado()));
    }
}
