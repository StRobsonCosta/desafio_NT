package br.com.desafio.voto.controller;

import br.com.desafio.voto.dto.ResultadoVotacaoDTO;
import br.com.desafio.voto.dto.VotoDTO;
import br.com.desafio.voto.enums.MetricsCounter;
import br.com.desafio.voto.enums.MetricsTimer;
import br.com.desafio.voto.model.Voto;
import br.com.desafio.voto.service.VotacaoService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/votacao")
@RequiredArgsConstructor
public class VotacaoController {

    private final VotacaoService votacaoService;
    private final MeterRegistry meterRegistry;
    private static final Long minutoDefault = 1L;

    @PostMapping("/sessao")
    public ResponseEntity<?> abrirSessao(@RequestParam UUID pautaId, @RequestParam(required = false) Long minutos) {
        Timer.Sample sample = Timer.start(meterRegistry);

        if (Objects.isNull(minutos))
            minutos = minutoDefault;

        votacaoService.abrirSessao(pautaId, minutos);

        sample.stop(meterRegistry.timer(MetricsTimer.ABRIR_SESSAO.getMetricName()));
        meterRegistry.counter(MetricsCounter.SESSOES_ABERTAS.getMetricName()).increment();

        String sessao = "Sessão iniciada às " + LocalDateTime.now() + " e durará " + minutos + " minutos.";
        return ResponseEntity.ok(sessao);
    }

    @Timed(value = "votacao_registrar_voto", description = "Tempo para registrar um voto")
    @PostMapping("/voto")
    public ResponseEntity<Voto> registrarVoto(@Valid @RequestBody VotoDTO votoDTO) {
        Timer.Sample sample = Timer.start(meterRegistry);

        Voto voto = votacaoService.registrarVoto(votoDTO);

        sample.stop(meterRegistry.timer(MetricsTimer.REGISTRAR_VOTO.getMetricName()));

        return ResponseEntity.status(HttpStatus.CREATED).body(voto);
    }

    @Timed(value = "votacao_calcular_resultado", description = "Tempo para calcular o resultado")
    @GetMapping("/resultado")
    public ResponseEntity<ResultadoVotacaoDTO> calcularResultado(@RequestParam UUID pautaId) {
        Timer.Sample sample = Timer.start(meterRegistry);

        ResultadoVotacaoDTO resultado = votacaoService.calcularResultado(pautaId);

        sample.stop(meterRegistry.timer(MetricsTimer.CALCULAR_RESULTADO.getMetricName()));
        meterRegistry.counter(MetricsCounter.CONSULTA_RESULTADO.getMetricName()).increment();

        return ResponseEntity.ok(resultado);
    }

    @Timed(value = "votacao_publicar_resultado", description = "Tempo para publicar o resultado")
    @PostMapping("/resultado/publicar")
    public ResponseEntity<Void> publicarResultado(@RequestParam UUID pautaId) {
        Timer.Sample sample = Timer.start(meterRegistry);

        votacaoService.publicarResultado(pautaId);

        sample.stop(meterRegistry.timer(MetricsTimer.PUBLICAR_RESULTADO.getMetricName()));
        meterRegistry.counter(MetricsCounter.RESULTADO_PUBLICACOES.getMetricName()).increment();

        return ResponseEntity.noContent().build();
    }

}
