package br.study.dynamodb.reactive.repository;

import br.study.dynamodb.reactive.DynamobdReativoApplication;
import br.study.dynamodb.reactive.dto.TransacaoDTO;
import br.study.dynamodb.reactive.model.Transacao;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.math.BigDecimal;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DynamobdReativoApplication.class)
@TestPropertySource(properties = { "amazon.dynamodb.endpoint=http://localhost:8000/", "amazon.aws.accesskey=test1", "amazon.aws.secretkey=test231" })
public class TransacaoRepositoryTest {

    @ClassRule
    public static LocalDbCreationRule dynamoDB = new LocalDbCreationRule();

    @Autowired
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @Autowired
    TransacaoRepository repository;

    private static final String EXPECTED_COST = "20";
    private static final String EXPECTED_PRICE = "50";

    @Before
    public void setup() throws Exception {


    }

    @Test
    public void test(){
        TransacaoDTO trx = TransacaoDTO
                .builder()
                .numeroTransacao("123456")
                .valor(new BigDecimal(10.22))
                .build();

        repository.saveTransacaoAsync(trx);

        StepVerifier
                .create(repository.saveTransacaoAsync(trx))
                .assertNext(transacaoDTO -> {
                    assertEquals("123457", transacaoDTO.getNumeroTransacao());
                })
                .expectComplete();
    }

}
