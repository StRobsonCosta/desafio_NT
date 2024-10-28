package br.com.desafio.voto.controller;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import br.com.desafio.voto.dto.PautaDTO;
import br.com.desafio.voto.service.PautaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PautaControllerTest {

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private PautaController pautaController;

    private UUID pautaId;
    private PautaDTO pautaDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        pautaId = UUID.randomUUID();
        pautaDTO = new PautaDTO();
    }

    @Test
    public void criarPauta_deveRetornarPautaCriadaComStatus201() {
        when(pautaService.criarPauta(pautaDTO)).thenReturn(pautaDTO);

        ResponseEntity<PautaDTO> response = pautaController.criarPauta(pautaDTO);

        verify(pautaService).criarPauta(pautaDTO);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(pautaDTO);
    }

    @Test
    public void buscarPauta_deveRetornarPautaComStatus200() {
        when(pautaService.buscarPautaDto(pautaId)).thenReturn(pautaDTO);

        ResponseEntity<PautaDTO> response = pautaController.buscarPauta(pautaId);

        verify(pautaService).buscarPautaDto(pautaId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(pautaDTO);
    }

    @Test
    public void listarPautas_deveRetornarListaDePautasComStatus200() {
        List<PautaDTO> pautas = List.of(pautaDTO);
        when(pautaService.listarPautasDto()).thenReturn(pautas);

        ResponseEntity<?> response = pautaController.listarPautas();

        verify(pautaService).listarPautasDto();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(pautas);
    }

    @Test
    public void deletarPauta_deveRemoverPautaComStatus204() {
        ResponseEntity<PautaDTO> response = pautaController.deletarPauta(pautaId);

        verify(pautaService).deletarPauta(pautaId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }
}
