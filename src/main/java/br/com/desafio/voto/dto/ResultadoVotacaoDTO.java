package br.com.desafio.voto.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultadoVotacaoDTO {

    private String descricaoPauta;
    private Long votosSim;
    private Long votosNao;
}
