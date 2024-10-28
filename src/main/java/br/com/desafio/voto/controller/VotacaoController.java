package br.com.desafio.voto.controller;

import br.com.desafio.voto.dto.ResultadoVotacaoDTO;
import br.com.desafio.voto.dto.VotoDTO;
import br.com.desafio.voto.model.Voto;
import br.com.desafio.voto.service.VotacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("api/votacao")
@RequiredArgsConstructor
public class VotacaoController {

    private final VotacaoService votacaoService;

    private static final Long minutoDefault = 1L;

    @PostMapping("/sessao")
    public ResponseEntity<?> abrirSessao(@RequestParam UUID pautaId, @RequestParam(required = false) Long minutos) {
        if(Objects.isNull(minutos))
            minutos = minutoDefault;

        votacaoService.abrirSessao(pautaId, minutos);
        String sessao = "Sessão iniciada às " + LocalDateTime.now() + " e durará " + minutos + " minutos.";
        return ResponseEntity.ok(sessao);
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

}
