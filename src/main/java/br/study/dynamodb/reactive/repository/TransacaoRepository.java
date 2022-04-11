package br.study.dynamodb.reactive.repository;

import br.study.dynamodb.reactive.builder.TransacaoBuilder;
import br.study.dynamodb.reactive.builder.TransacaoDTOBuilder;
import br.study.dynamodb.reactive.dto.OperacaoBandeiraDTO;
import br.study.dynamodb.reactive.dto.TransacaoDTO;
import br.study.dynamodb.reactive.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class TransacaoRepository {
    private DynamoDbAsyncClient dynamoDbClient;
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient;

    private static final String TABLE_NAME = "core";
    private static final String GSI1_INDEX = "GSI1";
    private static final String GSI2_INDEX = "GSI2";

    private TableSchema<Transacao> transacaoTableSchema;
    private DynamoDbAsyncTable<Transacao> transacaoTable;

    private TableSchema<TransacaoBandeira> operacaoBandeiraTableSchema;
    private DynamoDbAsyncTable<TransacaoBandeira> operacaoBandeiraTable;

    private TableSchema<Pagador> pagadorTableSchema;
    private DynamoDbAsyncTable<Pagador> pagadorDynamoDbTable;

    private TableSchema<Recebedor> recebedorTableSchema;
    private DynamoDbAsyncTable<Recebedor> recebedorDynamoDbTable;

    private TableSchema<Parceiro> parceiroTableSchema;
    private DynamoDbAsyncTable<Parceiro> parceiroDynamoDbTable;


    public TransacaoRepository(DynamoDbAsyncClient dynamoDbClient, DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoDbEnhancedClient = dynamoDbEnhancedAsyncClient;

        operacaoBandeiraTableSchema = TableSchema.fromClass(TransacaoBandeira.class);
        operacaoBandeiraTable = dynamoDbEnhancedClient.table(TABLE_NAME, operacaoBandeiraTableSchema);

        transacaoTableSchema = TableSchema.fromClass(Transacao.class);
        transacaoTable = dynamoDbEnhancedClient.table(TABLE_NAME, transacaoTableSchema);

        pagadorTableSchema = TableSchema.fromClass(Pagador.class);
        pagadorDynamoDbTable = dynamoDbEnhancedClient.table(TABLE_NAME, pagadorTableSchema);

        recebedorTableSchema = TableSchema.fromClass(Recebedor.class);
        recebedorDynamoDbTable = dynamoDbEnhancedClient.table(TABLE_NAME, recebedorTableSchema);

        parceiroTableSchema = TableSchema.fromClass(Parceiro.class);
        parceiroDynamoDbTable = dynamoDbEnhancedClient.table(TABLE_NAME, parceiroTableSchema);
    }

    public Mono<TransacaoDTO> saveTransacaoAsync(TransacaoDTO transacaoDTO) {

        CompletableFuture future = transacaoTable.putItem(TransacaoBuilder.buildTraansacao(transacaoDTO));

        if (transacaoDTO.getParceiro() != null) {
            future.thenCompose(s -> parceiroDynamoDbTable.putItem(TransacaoBuilder.buildParceiro(transacaoDTO.getParceiro(), transacaoDTO.getNumeroTransacao())));
        }

        if (transacaoDTO.getPull() != null) {
            future.thenCompose(s -> pagadorDynamoDbTable.putItem(TransacaoBuilder.buildPagador(transacaoDTO.getPull().getDadosPortador(), transacaoDTO.getNumeroTransacao())));
            future.thenCompose(s -> operacaoBandeiraTable.putItem(TransacaoBuilder.buildTransacaobandeira(transacaoDTO.getPull().getDadosBandeira(), transacaoDTO.getNumeroTransacao(), null, "PULL")));
        }

        if (transacaoDTO.getPush() != null) {
            for (OperacaoBandeiraDTO operacaoBandeiraDTO : transacaoDTO.getPush()) {
                String id = UUID.randomUUID().toString();
                future.thenCompose(s -> recebedorDynamoDbTable.putItem(TransacaoBuilder.buildRecebedor(operacaoBandeiraDTO.getDadosPortador(), transacaoDTO.getNumeroTransacao(), id)));
                future.thenCompose(s -> operacaoBandeiraTable.putItem(TransacaoBuilder.buildTransacaobandeira(operacaoBandeiraDTO.getDadosBandeira(), transacaoDTO.getNumeroTransacao(), id, "PUSH")));
            }
        }
        return Mono.fromFuture(future)
                .map(response -> {
                    return transacaoDTO;
                });
    }

    public Mono<TransacaoDTO> findTransacaoByNumeroTransacao(String numeroTransacao) {

        return Mono.fromFuture(getTransacaoByNumerotransacao(numeroTransacao))
                .map(response -> {
                    return loadTransacao(response.items());
                });
    }

    public Mono<TransacaoDTO> findTransacaoByIdsbandeiraAndEmissor(String idTransacaoBandeira, String emissor) {

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":emissor", AttributeValue.builder().s(emissor).build());
        eav.put(":sk", AttributeValue.builder().s("BID#" + idTransacaoBandeira).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .indexName(GSI2_INDEX)
                .keyConditionExpression("GSI2_PK =:emissor And GSI2_SK =:sk")
                .expressionAttributeValues(eav)
                .build();

        CompletableFuture<QueryResponse> transacao = dynamoDbClient.query(queryRequest)
                .thenCompose(transacoesbandeira -> {
                    if (transacoesbandeira.items().size() > 0 && transacoesbandeira.items().get(0) != null) {
                        String numeroTransacao = transacoesbandeira.items().get(0).get("numeroTransacao").s();
                        log.info("Quering transacao {}", numeroTransacao);
                        return getTransacaoByNumerotransacao(numeroTransacao);
                    }
                    return CompletableFuture.completedFuture(null);
                });


        return Mono.fromFuture(transacao)
                .map(response -> {
                    return loadTransacao(response.items());
                });
    }

    private TransacaoDTO loadTransacao(List<Map<String, AttributeValue>> items) {
        TransacaoDTOBuilder transacaoBuilder = new TransacaoDTOBuilder();
        for (Map<String, AttributeValue> item : items) {
            AttributeValue entidade = item.get("entidade");

            switch (entidade.s()) {
                case (Transacao.ENTIDADE):
                    Transacao transacao = transacaoTableSchema.mapToItem(item);
                    transacaoBuilder.transacaoToDTO(transacao);
                    break;
                case (TransacaoBandeira.ENTIDADE):
                    TransacaoBandeira operacaoBandeira = operacaoBandeiraTableSchema.mapToItem(item);
                    if ("PULL".equals(operacaoBandeira.getOperacao())) {
                        transacaoBuilder.addOperacaoPullToTransacao(operacaoBandeira);
                    } else {
                        transacaoBuilder.addTransacaoPush(operacaoBandeira);
                    }
                    break;
                case Pagador.ENTIDADE:
                    Pagador pagador = pagadorTableSchema.mapToItem(item);
                    transacaoBuilder.addPagadorToPullTransacao(pagador);
                    break;
                case Recebedor.ENTIDADE:
                    Recebedor recebedor = recebedorTableSchema.mapToItem(item);
                    transacaoBuilder.addRecebedor(recebedor);
                    break;
                case Parceiro.ENTIDADE:
                    Parceiro parceiro = parceiroTableSchema.mapToItem(item);
                    transacaoBuilder.addParceiroToTransacao(parceiro);
                    break;
            }
        }

        return transacaoBuilder.build();
    }


    private CompletableFuture<QueryResponse> getTransacaoByNumerotransacao(String numeroTransacao) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":idTransacao", AttributeValue.builder().s("TRXID#" + numeroTransacao).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("PK =:idTransacao")
                .expressionAttributeValues(eav).build();

        return dynamoDbClient.query(queryRequest);
    }

}
