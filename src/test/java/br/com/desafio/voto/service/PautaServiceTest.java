package br.com.desafio.voto.service;

import br.com.desafio.voto.dto.PautaDTO;
import br.com.desafio.voto.exception.VotosException;
import br.com.desafio.voto.model.Pauta;
import br.com.desafio.voto.repository.PautaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PautaServiceTest {

    @InjectMocks
    private PautaService pautaService;

    @Mock
    private PautaRepository pautaRepo;

    private PautaDTO pautaDTO;
    private Pauta pauta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pautaDTO = new PautaDTO(UUID.randomUUID(), "Descrição da Pauta");
        pauta = new Pauta();
        pauta.setDescricao(pautaDTO.getDescricao());
    }

    @Test
    void deveCriarPautaComSucesso() {
        when(pautaRepo.save(any(Pauta.class))).thenReturn(pauta);

        PautaDTO resultado = pautaService.criarPauta(pautaDTO);

        assertNotNull(resultado);
        assertEquals(pautaDTO.getDescricao(), resultado.getDescricao());
        verify(pautaRepo).save(any(Pauta.class));
    }

    @Test
    void deveLancarExcecaoQuandoPautaDTOForNulo() {
        assertThrows(VotosException.class, () -> pautaService.criarPauta(null));
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoForNulaOuVazia() {
        pautaDTO.setDescricao(null);

        assertThrows(VotosException.class, () -> pautaService.criarPauta(pautaDTO));

        pautaDTO.setDescricao("");

        assertThrows(VotosException.class, () -> pautaService.criarPauta(pautaDTO));
    }

    @Test
    void deveBuscarPautaComSucesso() {
        UUID pautaId = UUID.randomUUID();
        when(pautaRepo.findById(pautaId)).thenReturn(Optional.of(pauta));

        PautaDTO resultado = pautaService.buscarPauta(pautaId);

        assertNotNull(resultado);
        assertEquals(pauta.getDescricao(), resultado.getDescricao());
    }

    @Test
    void deveLancarExcecaoQuandoPautaNaoEncontrada() {
        UUID pautaId = UUID.randomUUID();
        when(pautaRepo.findById(pautaId)).thenReturn(Optional.empty());

        assertThrows(VotosException.class, () -> pautaService.buscarPauta(pautaId));
    }

    @Test
    void deveListarPautasComSucesso() {
        when(pautaRepo.findAll()).thenReturn(Collections.singletonList(pauta));

        List<PautaDTO> resultado = pautaService.listarPautas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(pauta.getDescricao(), resultado.get(0).getDescricao());
    }

    @Test
    void deveLancarExcecaoQuandoNaoHouverPautas() {
        when(pautaRepo.findAll()).thenReturn(Collections.emptyList());

        assertThrows(VotosException.class, () -> pautaService.listarPautas());
    }

    @Test
    void deveDeletarPautaComSucesso() {
        UUID pautaId = UUID.randomUUID();

        pautaService.deletarPauta(pautaId);

        verify(pautaRepo).deleteById(pautaId);
    }

    @Test
    void deveLancarExcecaoQuandoPautaIdForNuloAoDeletar() {
        assertThrows(VotosException.class, () -> pautaService.deletarPauta(null));
    }
}
