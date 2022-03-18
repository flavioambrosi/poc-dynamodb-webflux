package br.study.dynamodb.reactive.builder;

import br.study.dynamodb.reactive.dto.ParceiroDTO;
import br.study.dynamodb.reactive.dto.PessoaDTO;
import br.study.dynamodb.reactive.dto.TransacaoBandeiraDTO;
import br.study.dynamodb.reactive.dto.TransacaoDTO;
import br.study.dynamodb.reactive.model.*;

import java.util.UUID;

public class TransacaoBuilder {

    public static Transacao buildTraansacao(TransacaoDTO transacaoDTO){
        Transacao transacao = Transacao.builder()
                .numeroTransacao(transacaoDTO.getNumeroTransacao())
                .entidade(Transacao.ENTIDADE)
                .data(transacaoDTO.getData())
                .moeda(transacaoDTO.getMoeda())
                .situacao(transacaoDTO.getSituacao())
                .build();
        transacao.mountPkSk();
        return transacao;
    }

    public static Parceiro buildParceiro(ParceiroDTO parceiroDTO, String numeroTransacao){
        Parceiro parceiro = Parceiro.builder()
                .numeroTransacao(numeroTransacao)
                .entidade(Parceiro.ENTIDADE)
                .nome(parceiroDTO.getNome())
                .id(parceiroDTO.getId())
                .build();
        parceiro.mountPkSk();
        return parceiro;
    }

    public static Pagador buildPagador(PessoaDTO pessoaDTO, String numeroTransacao){
        Pagador pagador = Pagador.builder()
                .numeroTransacao(numeroTransacao)
                .bandeira(pessoaDTO.getBandeira())
                .cartaoTokenizado(pessoaDTO.getCartaoTokenizado())
                .entidade(Pagador.ENTIDADE)
                .emissor(pessoaDTO.getEmissor())
                .nome(pessoaDTO.getNome())
                .build();
        pagador.mountPkSk();
        return pagador;
    }

    public static Recebedor buildRecebedor(PessoaDTO pessoaDTO, String numeroTransacao, String id){
        Recebedor recebedor = Recebedor.builder()
                .numeroTransacao(numeroTransacao)
                .bandeira(pessoaDTO.getBandeira())
                .cartaoTokenizado(pessoaDTO.getCartaoTokenizado())
                .entidade(Recebedor.ENTIDADE)
                .emissor(pessoaDTO.getEmissor())
                .nome(pessoaDTO.getNome())
                .id(id)
                .build();
        recebedor.mountPkSk();
        return recebedor;
    }


    public static TransacaoBandeira buildTransacaobandeira(TransacaoBandeiraDTO transacaoBandeiraDTO, String numeroTransacao, String id, String operacao){
        TransacaoBandeira transacaoBandeira = TransacaoBandeira.builder()
                .idTransacaoBandeira(transacaoBandeiraDTO.getIdTransacaoBandeira())
                .codigoAprovacao(transacaoBandeiraDTO.getCodigoAprovacao())
                .operacao(operacao)
                .emissor(transacaoBandeiraDTO.getEmissor())
                .id(UUID.randomUUID().toString())
                .entidade(TransacaoBandeira.ENTIDADE)
                .numeroTransacao(numeroTransacao)
                .data(transacaoBandeiraDTO.getData())
                .id(id)
                .build();

        transacaoBandeira.mountPkSk();
        return transacaoBandeira;
    }
}
