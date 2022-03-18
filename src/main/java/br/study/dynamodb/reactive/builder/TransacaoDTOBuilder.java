package br.study.dynamodb.reactive.builder;

import br.study.dynamodb.reactive.dto.*;
import br.study.dynamodb.reactive.model.*;

public class TransacaoDTOBuilder {

    private TransacaoDTO transacaoDTO = new TransacaoDTO();

    public TransacaoDTO build(){
        return transacaoDTO;
    }

    public void transacaoToDTO(Transacao transacao){
        transacaoDTO.setNumeroTransacao(transacao.getNumeroTransacao());
        transacaoDTO.setData(transacao.getData());
        transacaoDTO.setValor(transacao.getValor());
        transacaoDTO.setMoeda(transacao.getMoeda());
        transacaoDTO.setSituacao(transacao.getSituacao());
    }

    public void addParceiroToTransacao(Parceiro parceiro){
        transacaoDTO.setParceiro(ParceiroDTO.builder()
                .id(parceiro.getId())
                .nome(parceiro.getNome())
                .build());
    }



    public void addPagadorToPullTransacao(Pagador pagador) {
        PessoaDTO pessoaDTO = PessoaDTO.builder()
                .bandeira(pagador.getBandeira())
                .emissor(pagador.getEmissor())
                .cartaoTokenizado(pagador.getCartaoTokenizado())
                .nome(pagador.getNome())
                .build();

        if(transacaoDTO.getPull() == null){
            transacaoDTO.setPull(new OperacaoBandeiraDTO());
        }
        transacaoDTO.getPull().setDadosPortador(pessoaDTO);
    }

    public void addOperacaoPullToTransacao(TransacaoBandeira transacaoBandeira){
        TransacaoBandeiraDTO transacaoBandeiraDTO = TransacaoBandeiraDTO.builder()
                .idTransacaoBandeira(transacaoBandeira.getIdTransacaoBandeira())
                .codigoAprovacao(transacaoBandeira.getCodigoAprovacao())
                .operacaoContabilizada(transacaoBandeira.getOperacaoContabilizada())
                .emissor(transacaoBandeira.getEmissor())
                .data(transacaoBandeira.getData())
                .build();

        if(transacaoDTO.getPull() == null){
            transacaoDTO.setPull(new OperacaoBandeiraDTO());
        }
        transacaoDTO.getPull().setDadosBandeira(transacaoBandeiraDTO);
    }

    public void addRecebedor(Recebedor recebedor) {
        PessoaDTO pessoaDTO = PessoaDTO.builder()
                .bandeira(recebedor.getBandeira())
                .emissor(recebedor.getEmissor())
                .cartaoTokenizado(recebedor.getCartaoTokenizado())
                .nome(recebedor.getNome())
                .build();
        transacaoDTO.addRecebedorOperacaoPushPorId(recebedor.getId(), pessoaDTO);
    }

    public void addTransacaoPush(TransacaoBandeira transacaoBandeira) {
        TransacaoBandeiraDTO transacaoBandeiraDTO = TransacaoBandeiraDTO.builder()
                .idTransacaoBandeira(transacaoBandeira.getIdTransacaoBandeira())
                .codigoAprovacao(transacaoBandeira.getCodigoAprovacao())
                .operacaoContabilizada(transacaoBandeira.getOperacaoContabilizada())
                .emissor(transacaoBandeira.getEmissor())
                .data(transacaoBandeira.getData())
                .build();

        transacaoDTO.addDadosbandeiraOperacaoPush(transacaoBandeira.getId(), transacaoBandeiraDTO);
    }
}
