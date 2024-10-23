package br.com.desafio.voto.controller;

import br.com.desafio.voto.dto.PautaDTO;
import br.com.desafio.voto.service.PautaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/pauta")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;

    @PostMapping()
    public ResponseEntity<PautaDTO> criarPauta(@RequestBody @Valid PautaDTO pautaDTO) {
        PautaDTO pauta = pautaService.criarPauta(pautaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pauta);
    }

    @GetMapping("/")
    public ResponseEntity<PautaDTO> buscarPauta(@RequestParam UUID pautaId) {
        PautaDTO pauta = pautaService.buscarPauta(pautaId);
        return ResponseEntity.ok(pauta);
    }

    @GetMapping()
    public ResponseEntity<?> listarPautas() {
        List<PautaDTO> pautas = pautaService.listarPautas();
        return ResponseEntity.ok(pautas);
    }

    @DeleteMapping("/")
    public ResponseEntity<PautaDTO> deletarPauta(@RequestParam UUID pautaId) {
        pautaService.deletarPauta(pautaId);
        return ResponseEntity.noContent().build();
    }

}
