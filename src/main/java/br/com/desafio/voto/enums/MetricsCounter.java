package br.com.desafio.voto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MetricsCounter {

    SESSOES_ABERTAS("votacao_sessao_abertas"),
    CONSULTA_RESULTADO("votacao_resultado_consulta"),
    RESULTADO_PUBLICACOES("votacao_resultado_publicacoes"),

    VOTOS_ABSTENCAO("votacao_percentual_abstencao"),
    VOTOS_TOTAL("votacao_total_votos"),
    VOTOS_SIM("votacao_sim"),
    VOTOS_NAO("votacao_nao");

    private final String metricName;

}
