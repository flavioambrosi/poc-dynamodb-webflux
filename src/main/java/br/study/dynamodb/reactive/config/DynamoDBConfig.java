package br.study.dynamodb.reactive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDBConfig {

  @Value("${dynamodb.region}")
  private String dynamodbRegion;

  @Value("${dynamodb.endpoint}")
  private String dynamodbEndpoint;

  @Bean
  public DynamoDbAsyncClient getDynamoDbAsyncClient() {

    return DynamoDbAsyncClient
            .builder()
            .region(Region.of(dynamodbRegion))
            .endpointOverride(URI.create(dynamodbEndpoint))
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .build();
  }

  @Bean
  public DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient() {
    return DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(getDynamoDbAsyncClient()).build();
  }

  @Bean
  public DynamoDbClient getDynamoDbClient() {
    return DynamoDbClient
            .builder()
            .region(Region.of(dynamodbRegion))
            .endpointOverride(URI.create(dynamodbEndpoint))
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .build();
  }


  @Bean
  public DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
    return DynamoDbEnhancedClient.builder().dynamoDbClient(getDynamoDbClient()).build();
  }
}
