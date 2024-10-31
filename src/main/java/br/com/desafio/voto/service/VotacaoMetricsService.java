package br.com.desafio.voto.service;

import br.com.desafio.voto.enums.MetricsCounter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VotacaoMetricsService {

    private final Counter votosSimCounter;
    private final Counter votosNaoCounter;
    private final Timer tempoSessaoAbertaTimer;
    private final MeterRegistry meterRegistry;
    private final AssociadoService associadoService;

    @Autowired
    public VotacaoMetricsService(MeterRegistry meterRegistry, AssociadoService associadoService, Timer tempoSessaoAbertaTimer) {
        this.tempoSessaoAbertaTimer = tempoSessaoAbertaTimer;
        this.meterRegistry = meterRegistry;
        this.associadoService = associadoService;

        this.votosSimCounter = Counter.builder(MetricsCounter.VOTOS_SIM.getMetricName())
                .description("Quantidade de votos SIM")
                .register(meterRegistry);

        this.votosNaoCounter = Counter.builder(MetricsCounter.VOTOS_NAO.getMetricName())
                .description("Quantidade de votos NÃO")
                .register(meterRegistry);

        Gauge.builder(MetricsCounter.VOTOS_ABSTENCAO.getMetricName(), this,
                        VotacaoMetricsService::calcularAbstencao)
                .description("Percentual de abstenção")
                .register(meterRegistry);
    }

    public void incrementarVotoSim() {
        votosSimCounter.increment();
        log.info("Incrementando voto SIM. Total atual: {}", votosSimCounter.count());
    }

    public void incrementarVotoNao() {
        votosNaoCounter.increment();
        log.info("Incrementando voto NÃO. Total atual: {}", votosNaoCounter.count());
    }

    public Double calcularAbstencao() {
        Long totalAssociados = associadoService.getTotalAssociados();
        Long totalVotos = (long) (votosSimCounter.count() + votosNaoCounter.count());
        return (totalAssociados - totalVotos) * 100.0 / totalAssociados;
    }

    public void incrementarContadorDeVotos() {
        meterRegistry.counter(MetricsCounter.VOTOS_TOTAL.getMetricName()).increment();
    }

    public Timer.Sample iniciarTempoSessao() {
        return Timer.start(meterRegistry);
    }

    public void registrarFimSessao(Timer.Sample sample) {
        sample.stop(tempoSessaoAbertaTimer);
    }

    public void registrarTempoCustomizado(Timer.Sample sample, Long duracao, TimeUnit unidade) {
        tempoSessaoAbertaTimer.record(duracao, unidade);
        sample.stop(tempoSessaoAbertaTimer);
    }
}
