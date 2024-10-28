package br.com.desafio.voto.controller;

import br.com.desafio.voto.dto.AssociadoDto;
import br.com.desafio.voto.service.AssociadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/associado")
@RequiredArgsConstructor
public class AssociadoController {

    private final AssociadoService associadoService;

    @PostMapping()
    public ResponseEntity<AssociadoDto> criarAssociado (@RequestBody @Valid AssociadoDto dto) {
        AssociadoDto associado = associadoService.criarAssociado(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(associado);
    }

    @GetMapping("/")
    public ResponseEntity<AssociadoDto> buscarAssociado(@RequestParam UUID associadoId) {
        AssociadoDto dto = associadoService.buscarAssociadoDto(associadoId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping()
    public ResponseEntity<?> listarAssociados() {
        List<AssociadoDto> associados = associadoService.listarAssocidadosDto();
        return ResponseEntity.ok(associados);
    }

    @DeleteMapping("/")
    public ResponseEntity<AssociadoDto> deletarAssociado(@RequestParam UUID associadoId) {
        associadoService.deletarAssociado(associadoId);
        return ResponseEntity.noContent().build();
    }
}
