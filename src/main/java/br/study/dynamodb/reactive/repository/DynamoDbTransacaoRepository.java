package br.study.dynamodb.reactive.repository;

import br.study.dynamodb.reactive.builder.TransacaoBuilder;
import br.study.dynamodb.reactive.builder.TransacaoDTOBuilder;
import br.study.dynamodb.reactive.dto.EmissorDTO;
import br.study.dynamodb.reactive.dto.OperacaoBandeiraDTO;
import br.study.dynamodb.reactive.dto.TransacaoDTO;
import br.study.dynamodb.reactive.model.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.net.URI;
import java.util.*;

@Service
public class DynamoDbTransacaoRepository {

    private static final String TABLE_NAME = "core";
    private static final String GSI1_INDEX = "GSI1";
    private static final String GSI2_INDEX = "GSI2";

    private DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private DynamoDbClient dynamoDbClient;

    private TableSchema<Transacao> transacaoTableSchema;
    private DynamoDbTable<Transacao> transacaoTable;

    private TableSchema<TransacaoBandeira> operacaoBandeiraTableSchema;
    private DynamoDbTable<TransacaoBandeira> operacaoBandeiraTable;

    private TableSchema<Pagador> pagadorTableSchema;
    private DynamoDbTable<Pagador> pagadorDynamoDbTable;

    private TableSchema<Recebedor> recebedorTableSchema;
    private DynamoDbTable<Recebedor> recebedorDynamoDbTable;

    private TableSchema<Parceiro> parceiroTableSchema;
    private DynamoDbTable<Parceiro> parceiroDynamoDbTable;

    public DynamoDbTransacaoRepository() {
        dynamoDbClient = DynamoDbClient
                .builder()
                .region(Region.of("sa-east-1"))
                .endpointOverride(URI.create("http://localhost:4569"))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();

        dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();

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


    public TransacaoDTO findTransacaoPorIdBandeiraEmissor(String idBandeira, String emissor) {
        TransacaoDTO transacaoDTO = null;
        AttributeValue idBandeiraPK = AttributeValue.builder()
                .s(TransacaoBandeira.prefixedId(idBandeira))
                .build();

        AttributeValue emissorSK = AttributeValue.builder()
                .s(emissor)
                .build();

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .indexName(GSI1_INDEX)
                .keyConditionExpression("GSI1_PK = :idBandeira And GSI1_SK = :emissor")
                .expressionAttributeValues(Map.of(":idBandeira", idBandeiraPK, ":emissor", emissorSK))
                .build();

        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        if (!queryResponse.items().isEmpty()) {
            String idTransacao = queryResponse.items().get(0).get("PK").s();
            transacaoDTO = this.findTransacao(idTransacao);
        }
        return transacaoDTO;
    }


    public TransacaoDTO findTransacao(String idTransacao) {

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":idTransacao", AttributeValue.builder().s(idTransacao).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("PK =:idTransacao")
                .expressionAttributeValues(eav).build();

        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResponse.items();
        return loadTransacao(items);
    }

    public TransacaoDTO findTransacaobyNumerotransacao(String numerotransacao) {
        return findTransacao("TRXID#" + numerotransacao);
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
                    if ("PULL" .equals(operacaoBandeira.getOperacao())) {
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

    public TransacaoDTO saveTransacao(TransacaoDTO transacaoDTO) {
        transacaoTable.putItem(TransacaoBuilder.buildTraansacao(transacaoDTO));

        if (transacaoDTO.getParceiro() != null) {
            parceiroDynamoDbTable.putItem(TransacaoBuilder.buildParceiro(transacaoDTO.getParceiro(), transacaoDTO.getNumeroTransacao()));
        }

        if (transacaoDTO.getPull() != null) {
            pagadorDynamoDbTable.putItem(TransacaoBuilder.buildPagador(transacaoDTO.getPull().getDadosPortador(), transacaoDTO.getNumeroTransacao()));
            operacaoBandeiraTable.putItem(TransacaoBuilder.buildTransacaobandeira(transacaoDTO.getPull().getDadosBandeira(), transacaoDTO.getNumeroTransacao(), null, "PULL"));
        }

        if (transacaoDTO.getPush() != null) {
            for (OperacaoBandeiraDTO operacaoBandeiraDTO : transacaoDTO.getPush()) {
                String id = UUID.randomUUID().toString();
                recebedorDynamoDbTable.putItem(TransacaoBuilder.buildRecebedor(operacaoBandeiraDTO.getDadosPortador(), transacaoDTO.getNumeroTransacao(), id));
                operacaoBandeiraTable.putItem(TransacaoBuilder.buildTransacaobandeira(operacaoBandeiraDTO.getDadosBandeira(), transacaoDTO.getNumeroTransacao(), id, "PUSH"));
            }
        }

        return null;
    }

    public List<EmissorDTO> findTransacaoByIdsbandeiraAndEmissores(List<String> ids, List<String> emissores) {

        List<EmissorDTO> emissoresDto = new ArrayList<>();
        for (String emissor : emissores) {

            Map<String, AttributeValue> eav = new HashMap<>();
            eav.put(":emissor", AttributeValue.builder().s(emissor).build());
            eav.put(":sk", AttributeValue.builder().s("BID#").build());

            Map<String, AttributeValue> idsMap = generateInClause(ids);
            eav.putAll(idsMap);

            String inClause = ""; //'StringUtils.join(idsMap.keySet(), ",");

            QueryRequest queryRequest = QueryRequest.builder()
                    .tableName(TABLE_NAME)
                    .indexName(GSI2_INDEX)
                    .keyConditionExpression("GSI2_PK =:emissor And begins_with(GSI2_SK, :sk) ")
                    .filterExpression("idTransacaoBandeira in(" + inClause + ")")
                    .expressionAttributeValues(eav)
                    .build();
            QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

            List<TransacaoDTO> transacoes = new ArrayList<>();
            for (Map<String, AttributeValue> item : queryResponse.items()) {
                transacoes.add(findTransacao(item.get("PK").s()));
            }
            emissoresDto.add(EmissorDTO.builder().nomeEmissor(emissor).transacoes(transacoes).total(transacoes.size()).build());
        }

        return emissoresDto;
    }

    private Map<String, AttributeValue> generateInClause(List<String> values){
        Map<String, AttributeValue> map = new HashMap<>();
        int count = 0;
        for (String value : values) {
            map.put(":id"+count++, AttributeValue.builder().s(value).build());
        }
        return map;
    }


}
