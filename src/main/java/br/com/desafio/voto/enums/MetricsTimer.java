package br.com.desafio.voto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MetricsTimer {
    ABRIR_SESSAO("votacao_abrir_sessao_latencia"),
    REGISTRAR_VOTO("votacao_voto_latencia"),
    CALCULAR_RESULTADO("votacao_resultado_latencia"),
    PUBLICAR_RESULTADO("votacao_publicar_latencia");

    private final String metricName;
}