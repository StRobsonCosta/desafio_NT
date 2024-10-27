package br.com.desafio.voto.controller;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import br.com.desafio.voto.dto.ResultadoVotacaoDTO;
import br.com.desafio.voto.dto.VotoDTO;
import br.com.desafio.voto.model.Voto;
import br.com.desafio.voto.service.VotacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class VotacaoControllerTest {

    @Mock
    private VotacaoService votacaoService;

    @InjectMocks
    private VotacaoController votacaoController;

    private UUID pautaId;
    private VotoDTO votoDTO;
    private Voto voto;
    private ResultadoVotacaoDTO resultado;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        UUID associadoId = UUID.randomUUID();
        pautaId = UUID.randomUUID();
        votoDTO = new VotoDTO(associadoId, pautaId, true);
        voto = new Voto();
        resultado = new ResultadoVotacaoDTO("Pauta Teste", 12L, 134L);
    }

    @Test
    public void abrirSessao_semMinutosInformadosDeveUsarValorPadraoEStatus200() {
        ResponseEntity<?> response = votacaoController.abrirSessao(pautaId, null);

        verify(votacaoService).abrirSessao(pautaId, 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(String.class);
        assertThat(response.getBody().toString()).contains("Sessão iniciada às");
    }

    @Test
    public void abrirSessao_comMinutosInformadosDeveUsarValorCorretoEStatus200() {
        ResponseEntity<?> response = votacaoController.abrirSessao(pautaId, 5L);

        verify(votacaoService).abrirSessao(pautaId, 5L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void registrarVoto_deveSalvarVotoERetornarComStatus201() {
        when(votacaoService.registrarVoto(votoDTO)).thenReturn(voto);

        ResponseEntity<Voto> response = votacaoController.registrarVoto(votoDTO);

        verify(votacaoService).registrarVoto(votoDTO);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(voto);
    }

    @Test
    public void calcularResultado_deveRetornarResultadoComStatus200() {
        when(votacaoService.calcularResultado(pautaId)).thenReturn(resultado);

        ResponseEntity<ResultadoVotacaoDTO> response = votacaoController.calcularResultado(pautaId);

        verify(votacaoService).calcularResultado(pautaId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(resultado);
    }

    @Test
    public void publicarResultado_devePublicarERetornarComStatus204() {
        ResponseEntity<Void> response = votacaoController.publicarResultado(pautaId);

        verify(votacaoService).publicarResultado(pautaId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
