package br.com.desafio.voto.service;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.desafio.voto.dto.AssociadoDto;
import br.com.desafio.voto.enums.ErroMensagem;
import br.com.desafio.voto.exception.VotosException;
import br.com.desafio.voto.model.Associado;
import br.com.desafio.voto.repository.AssociadoRepository;
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
import static org.mockito.Mockito.*;

public class AssociadoServiceTest {

    @Mock
    private AssociadoRepository associadoRepo;

    @Mock
    private CpfValidationService cpfValidation;

    @InjectMocks
    private AssociadoService associadoService;

    private AssociadoDto associadoDto;

    @Mock
    private CPFValidator cpfValidator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        associadoDto = new AssociadoDto(null, "João Silva", "12345678901");
    }

    @Test
    void criarAssociado_DeveRetornarAssociadoDto_QuandoCpfValido() {
        when(cpfValidation.cpfValido(associadoDto.getCpf())).thenReturn(true);
        Associado associado = new Associado(UUID.randomUUID(), "João Silva", "12345678901");
        when(associadoRepo.save(any(Associado.class))).thenReturn(associado);

        AssociadoDto resultado = associadoService.criarAssociado(associadoDto);

        assertNotNull(resultado);
        assertEquals(associado.getNome(), resultado.getNome());
        assertEquals(associado.getCpf(), resultado.getCpf());
        verify(associadoRepo, times(1)).save(any(Associado.class));
    }

    @Test
    void criarAssociado_DeveLancarVotosException_QuandoCpfInvalido() {
        when(cpfValidation.cpfValido(associadoDto.getCpf())).thenReturn(false);
        associadoDto.setCpf("cpf-invalido");

        VotosException exception = assertThrows(VotosException.class, () ->
                associadoService.criarAssociado(associadoDto)
        );
        assertEquals(ErroMensagem.CPF_INVALIDO.getMensagem(), exception.getMessage());
        verify(associadoRepo, never()).save(any(Associado.class));
    }

    @Test
    void buscarAssociado_DeveRetornarAssociadoDto_QuandoAssociadoDtoExiste() {

        UUID associadoId = UUID.randomUUID();
        Associado associado = new Associado(associadoId, "João Silva", "12345678901");
        when(associadoRepo.findById(associadoId)).thenReturn(Optional.of(associado));

        AssociadoDto resultado = associadoService.buscarAssociadoDto(associadoId);

        assertNotNull(resultado);
        assertEquals(associadoId, resultado.getId());
        verify(associadoRepo, times(1)).findById(associadoId);
    }

    @Test
    void buscarAssociado_DeveLancarVotosException_QuandoAssociadoDtoNaoEncontrado() {

        UUID associadoId = UUID.randomUUID();
        when(associadoRepo.findById(associadoId)).thenReturn(Optional.empty());

        VotosException exception = assertThrows(VotosException.class, () ->
                associadoService.buscarAssociadoDto(associadoId)
        );
        assertEquals(ErroMensagem.ASSOCIADO_NAO_ENCONTRADO.getMensagem(), exception.getMessage());
    }

    @Test
    void buscarAssociado_DeveRetornarAssociado_QuandoAssociadoExiste() {

        UUID associadoId = UUID.randomUUID();
        Associado associado = new Associado(associadoId, "João Silva", "12345678901");
        when(associadoRepo.findById(associadoId)).thenReturn(Optional.of(associado));

        Associado resultado = associadoService.buscarAssociado(associadoId);

        assertNotNull(resultado);
        assertEquals(associadoId, resultado.getId());
        verify(associadoRepo, times(1)).findById(associadoId);
    }

    @Test
    void buscarAssociado_DeveLancarVotosException_QuandoAssociadoNaoEncontrado() {

        UUID associadoId = UUID.randomUUID();
        when(associadoRepo.findById(associadoId)).thenReturn(Optional.empty());

        VotosException exception = assertThrows(VotosException.class, () ->
                associadoService.buscarAssociado(associadoId)
        );
        assertEquals(ErroMensagem.ASSOCIADO_NAO_ENCONTRADO.getMensagem(), exception.getMessage());
    }

    @Test
    void listarAssociados_DeveRetornarListaAssociadoDto_QuandoAssociadosExistem() {

        List<Associado> associados = List.of(
                new Associado(UUID.randomUUID(), "João Silva", "12345678901"),
                new Associado(UUID.randomUUID(), "Maria Santos", "09876543210")
        );
        when(associadoRepo.findAll()).thenReturn(associados);
        List<AssociadoDto> resultado = associadoService.listarAssocidadosDto();

        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
        verify(associadoRepo, times(1)).findAll();
    }

    @Test
    void listarAssociados_DeveLancarVotosException_QuandoNaoExistemAssociados() {

        when(associadoRepo.findAll()).thenReturn(Collections.emptyList());

        VotosException exception = assertThrows(VotosException.class, () ->
                associadoService.listarAssocidadosDto()
        );
        assertEquals(ErroMensagem.ASSOCIADO_NAO_ENCONTRADO.getMensagem(), exception.getMessage());
    }

    @Test
    void deletarAssociado_DeveChamarDeleteById_QuandoAssociadoIdValido() {

        UUID associadoId = UUID.randomUUID();

        associadoService.deletarAssociado(associadoId);

        verify(associadoRepo, times(1)).deleteById(associadoId);
    }

    @Test
    void deletarAssociado_DeveLancarVotosException_QuandoAssociadoIdNulo() {

        VotosException exception = assertThrows(VotosException.class, () ->
                associadoService.deletarAssociado(null)
        );
        assertEquals(ErroMensagem.ASSOCIADO_NAO_ENCONTRADO.getMensagem(), exception.getMessage());
    }

    @Test
    void validarCpfAssociado_DeveLancarVotosException_QuandoCpfNulo() {
        associadoDto.setCpf(null);

        VotosException exception = assertThrows(VotosException.class, () ->
                associadoService.validarCpfAssociado(associadoDto)
        );
        assertEquals(ErroMensagem.CPF_NAO_ENCONTRADO.getMensagem(), exception.getMessage());
    }

    @Test
    void validarCpfAssociado_DeveLancarVotosException_QuandoCpfInvalido() {
        when(cpfValidation.cpfValido(associadoDto.getCpf())).thenReturn(false);

        VotosException exception = assertThrows(VotosException.class, () ->
                associadoService.validarCpfAssociado(associadoDto)
        );
        assertEquals(ErroMensagem.CPF_INVALIDO.getMensagem(), exception.getMessage());
    }

    @Test
    void validarCpfAssociado_DeveLancarVotosException_QuandoCpfJaCadastrado() {
        when(cpfValidation.cpfValido(associadoDto.getCpf())).thenReturn(true);
        when(associadoRepo.existsByCpf(associadoDto.getCpf())).thenReturn(true);

        VotosException exception = assertThrows(VotosException.class, () ->
                associadoService.validarCpfAssociado(associadoDto)
        );
        assertEquals(ErroMensagem.CPF_JA_CADASTRADO.getMensagem(), exception.getMessage());
    }
}
