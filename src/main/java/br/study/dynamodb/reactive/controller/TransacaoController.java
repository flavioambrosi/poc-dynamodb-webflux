package br.study.dynamodb.reactive.controller;

import br.study.dynamodb.reactive.dto.FindTransacaoParams;
import br.study.dynamodb.reactive.dto.TransacaoDTO;
import br.study.dynamodb.reactive.repository.TransacaoRepository;
import br.study.dynamodb.reactive.response.TransacaoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transacao")
@Slf4j
public class TransacaoController {

    @Autowired
    private TransacaoRepository repository;

    @PostMapping
    public Mono<TransacaoDTO> saveTransacao(
            @RequestBody TransacaoDTO transacaoDTO){
        log.info("Save transacao");
        return repository.saveTransacaoAsync(transacaoDTO);
    }

    @GetMapping("/consulta")
    public Mono<TransacaoDTO> findTransacaoByNumeroTransacao(
            @RequestParam String numerotransacao){
        return repository.findTransacaoByNumeroTransacao(numerotransacao);
    }

    @GetMapping()
    public  Mono<TransacaoDTO> findTransacaoByIdsbandeiraAndEmissores(@RequestParam String idTransacaoBandeira, @RequestParam  String emissor){
        return repository.findTransacaoByIdsbandeiraAndEmissor(idTransacaoBandeira, emissor);

    }

    @GetMapping("/transacaoes")
    public Flux<TransacaoResponse> findTransacaoByIdsbandeiraAndEmissoresFlux(@RequestBody FindTransacaoParams findTransacaoParams){

        return getTransacoes(findTransacaoParams)
                .flatMap( transacaoDTO -> {
                    return Mono.just(TransacaoResponse.builder().numeroTransacao(transacaoDTO.getNumeroTransacao())
                            .moeda(transacaoDTO.getMoeda())
                            .valor(transacaoDTO.getValor())
                            .build());
                });
    }

    private Flux<TransacaoDTO> getTransacoes(FindTransacaoParams findTransacaoParams) {
        return Flux.fromIterable(findTransacaoParams.getEmissores())
                .flatMap(idEmissor -> {
                    return Flux.fromIterable(findTransacaoParams.getIdsTransacaoBandeira())
                            .flatMap(idTransacaoBandeira -> {
                               return  repository.findTransacaoByIdsbandeiraAndEmissor(idTransacaoBandeira, idEmissor);
                            });
                });
    }
}
