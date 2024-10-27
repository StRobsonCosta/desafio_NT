package br.com.desafio.voto.controller;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import br.com.desafio.voto.dto.AssociadoDto;
import br.com.desafio.voto.service.AssociadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AssociadoControllerTest {

    @Mock
    private AssociadoService associadoService;

    @InjectMocks
    private AssociadoController associadoController;

    private UUID associadoId;
    private AssociadoDto associadoDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        associadoId = UUID.randomUUID();
        associadoDto = new AssociadoDto();
    }

    @Test
    public void criarAssociado_deveRetornarAssociadoCriadoComStatus201() {
        when(associadoService.criarAssociado(associadoDto)).thenReturn(associadoDto);

        ResponseEntity<AssociadoDto> response = associadoController.criarAssociado(associadoDto);

        verify(associadoService).criarAssociado(associadoDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(associadoDto);
    }

    @Test
    public void buscarAssociado_deveRetornarAssociadoComStatus200() {
        when(associadoService.buscarAssociado(associadoId)).thenReturn(associadoDto);

        ResponseEntity<AssociadoDto> response = associadoController.buscarAssociado(associadoId);

        verify(associadoService).buscarAssociado(associadoId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(associadoDto);
    }

    @Test
    public void listarAssociados_deveRetornarListaDeAssociadosComStatus200() {
        List<AssociadoDto> associados = List.of(associadoDto);
        when(associadoService.listarAssocidados()).thenReturn(associados);

        ResponseEntity<?> response = associadoController.listarAssociados();

        verify(associadoService).listarAssocidados();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(associados);
    }

    @Test
    public void deletarAssociado_deveRemoverAssociadoComStatus204() {
        ResponseEntity<AssociadoDto> response = associadoController.deletarAssociado(associadoId);

        verify(associadoService).deletarAssociado(associadoId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }
}
