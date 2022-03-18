package br.study.dynamodb.reactive.controller;

import br.study.dynamodb.reactive.dto.EmissorDTO;
import br.study.dynamodb.reactive.dto.FindTransacaoParams;
import br.study.dynamodb.reactive.dto.TransacaoDTO;
import br.study.dynamodb.reactive.repository.DynamoDbTransacaoRepository;
import br.study.dynamodb.reactive.repository.TransacaoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transacao")
@Slf4j
public class TransacaoController {

    @Autowired
    private TransacaoRepository repository;

    @Autowired
    private DynamoDbTransacaoRepository dynamoDbTransacaoRepository;

    @PostMapping
    public Mono<TransacaoDTO> saveTransacao(
            @RequestBody TransacaoDTO transacaoDTO){
        log.info("Save transacao");
        return repository.saveTransacaAsync(transacaoDTO);
    }

    @GetMapping("/consulta")
    public ResponseEntity<TransacaoDTO> findTransacaoByNumeroTransacao(
            @RequestParam String numerotransacap){
        return new ResponseEntity(repository.findTransacaoByNumeroTransacao(numerotransacap), HttpStatus.OK);
    }

    @GetMapping("/transacaoes")
    public Mono<TransacaoDTO> findTransacaoByIdsbandeiraAndEmissores(@RequestBody FindTransacaoParams findTransacaoParams){
        return repository.findTransacaoByIdsbandeiraAndEmissor(findTransacaoParams.getIdsTransacaoBandeira().get(0), findTransacaoParams.getEmissores().get(0));

    }
}
