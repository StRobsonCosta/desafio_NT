package br.com.desafio.voto.service;

import br.com.desafio.voto.enums.MetricsCounter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class VotacaoMetricServiceTest {

    private SimpleMeterRegistry meterRegistry;
    private VotacaoMetricsService votacaoMetricsService;

    @Mock
    private AssociadoService associadoService;

    @Mock
    private Timer tempoSessaoAbertaTimer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        meterRegistry = new SimpleMeterRegistry();
        votacaoMetricsService = new VotacaoMetricsService(meterRegistry, associadoService, tempoSessaoAbertaTimer);
    }

    @Test
    void deveIncrementarVotoSim() {
        votacaoMetricsService.incrementarVotoSim();
        Counter votosSimCounter = meterRegistry.find(MetricsCounter.VOTOS_SIM.getMetricName()).counter();

        assertThat(votosSimCounter).isNotNull();
        assertThat(votosSimCounter.count()).isEqualTo(1.0);
    }

    @Test
    void deveIncrementarVotoNao() {
        votacaoMetricsService.incrementarVotoNao();
        Counter votosNaoCounter = meterRegistry.find(MetricsCounter.VOTOS_NAO.getMetricName()).counter();

        assertThat(votosNaoCounter).isNotNull();
        assertThat(votosNaoCounter.count()).isEqualTo(1.0);
    }

    @Test
    void deveCalcularAbstencaoCorretamente() {
        when(associadoService.getTotalAssociados()).thenReturn(10L);
        votacaoMetricsService.incrementarVotoSim();
        votacaoMetricsService.incrementarVotoNao();

        Double abstencao = votacaoMetricsService.calcularAbstencao();

        assertThat(abstencao).isEqualTo(80.0);
    }

    @Test
    void deveRegistrarTempoSessaoAberta() {
        Timer.Sample sample = votacaoMetricsService.iniciarTempoSessao();
        sample.stop(meterRegistry.timer(MetricsCounter.SESSOES_ABERTAS.getMetricName()));

        Timer tempoSessaoAbertaTimer = meterRegistry.find(MetricsCounter.SESSOES_ABERTAS.getMetricName()).timer();

        assertThat(tempoSessaoAbertaTimer).isNotNull();
        assertThat(tempoSessaoAbertaTimer.count()).isEqualTo(1);
    }

    @Test
    void deveRegistrarFimSessao() {
        Timer.Sample sample = Timer.start(meterRegistry);

        votacaoMetricsService.registrarFimSessao(sample);

        verify(tempoSessaoAbertaTimer, times(1)).record(anyLong(), eq(TimeUnit.NANOSECONDS));
    }

    @Test
    void deveRegistrarTempoCustomizado() {
        Timer.Sample sample = Timer.start(meterRegistry);
        Long duracao = 500L;
        TimeUnit unidade = TimeUnit.MILLISECONDS;

        votacaoMetricsService.registrarTempoCustomizado(sample, duracao, unidade);

        verify(tempoSessaoAbertaTimer).record(duracao, unidade);
        verify(tempoSessaoAbertaTimer, times(1)).record(duracao, unidade);
    }

    @Test
    void testIncrementarContadorDeVotos() {
        votacaoMetricsService.incrementarContadorDeVotos();
        Counter votosTotalCounter = meterRegistry.find(MetricsCounter.VOTOS_TOTAL.getMetricName()).counter();

        assertThat(votosTotalCounter).isNotNull();
        assertThat(votosTotalCounter.count()).isEqualTo(1.0);
    }

}
