package br.com.desafio.voto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResultadoVotacaoDTO {

    private String descricaoPauta;
    private Long votosSim;
    private Long votosNao;
}
