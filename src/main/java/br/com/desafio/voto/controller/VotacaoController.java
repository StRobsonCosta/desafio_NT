package br.com.desafio.voto.controller;

import br.com.desafio.voto.dto.PautaDTO;
import br.com.desafio.voto.dto.ResultadoVotacaoDTO;
import br.com.desafio.voto.dto.VotoDTO;
import br.com.desafio.voto.exception.VotosException;
import br.com.desafio.voto.model.SessaoVotacao;
import br.com.desafio.voto.model.Voto;
import br.com.desafio.voto.service.VotacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/votacao")
@RequiredArgsConstructor
public class VotacaoController {

    private final VotacaoService votacaoService;

    private static final Long minutoDefault = 1L;
    private static final String SESSAO_INICIADA = "Sessão Iniciada";

    @PostMapping("/pauta")
    public ResponseEntity<PautaDTO> criarPauta(@RequestBody @Valid PautaDTO pautaDTO) {
        PautaDTO pauta = votacaoService.criarPauta(pautaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pauta);
    }

    @GetMapping("/pauta/")
    public ResponseEntity<PautaDTO> buscarPauta(@RequestParam UUID pautaId) {
        PautaDTO pauta = votacaoService.buscarPauta(pautaId);
        return ResponseEntity.ok(pauta);
    }

    @GetMapping("/pauta")
    public ResponseEntity<?> listarPautas() {
        List<PautaDTO> pautas = votacaoService.listarPautas();
        return ResponseEntity.ok(pautas);
    }

    @PostMapping("/sessao")
    public ResponseEntity<?> abrirSessao(
            @RequestParam UUID pautaId,
            @RequestParam(required = false) Long minutos) {
        if(Objects.isNull(minutos))
            minutos = minutoDefault;

        votacaoService.abrirSessao(pautaId, minutos);
        return ResponseEntity.ok(SESSAO_INICIADA);
    }

    @PostMapping("/voto")
    public ResponseEntity<Voto> registrarVoto(@Valid @RequestBody VotoDTO votoDTO) {
        Voto voto = votacaoService.registrarVoto(votoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(voto);
    }

    @GetMapping("/resultado")
    public ResponseEntity<ResultadoVotacaoDTO> calcularResultado(@RequestParam UUID pautaId) {
        ResultadoVotacaoDTO resultado = votacaoService.calcularResultado(pautaId);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/resultado/publicar")
    public ResponseEntity<Void> publicarResultado(@RequestParam UUID pautaId) {
        votacaoService.publicarResultado(pautaId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(VotosException.class)
    public ResponseEntity<String> handleVotosException(VotosException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
