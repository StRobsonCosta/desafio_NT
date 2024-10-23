package br.com.desafio.voto.service;

import br.com.desafio.voto.dto.ResultadoVotacaoDTO;
import br.com.desafio.voto.dto.VotoDTO;
import br.com.desafio.voto.exception.VotosException;
import br.com.desafio.voto.model.Associado;
import br.com.desafio.voto.model.Pauta;
import br.com.desafio.voto.model.SessaoVotacao;
import br.com.desafio.voto.model.Voto;
import br.com.desafio.voto.repository.AssociadoRepository;
import br.com.desafio.voto.repository.PautaRepository;
import br.com.desafio.voto.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class VotacaoServiceTest {

    @InjectMocks
    private VotacaoService votacaoService;

    @Mock
    private PautaRepository pautaRepo;

    @Mock
    private VotoRepository votoRepo;

    @Mock
    private AssociadoRepository associadoRepo;

    @Mock
    private CpfValidationService cpfValidationService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private RedisTemplate<String, SessaoVotacao> redisTemplate;

    @Mock
    private ValueOperations<String, SessaoVotacao> valueOperations;

    private Pauta pauta;
    private Associado associado;
    private VotoDTO votoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UUID pautaId = UUID.randomUUID();
        UUID associadoId = UUID.randomUUID();

        pauta = new Pauta(pautaId, "Descrição da Pauta");
        associado = new Associado(associadoId, "12345678909", "Nome do Associado");
        votoDTO = new VotoDTO(associadoId, pautaId, true);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void deveAbrirSessaoComSucesso() {
        UUID pautaId = pauta.getId();
        when(pautaRepo.findById(pautaId)).thenReturn(Optional.of(pauta));

        votacaoService.abrirSessao(pautaId, 10);

        String key = "sessao:" + pautaId;
        verify(redisTemplate.opsForValue()).set(eq(key), any(SessaoVotacao.class), eq(10L), eq(TimeUnit.MINUTES));
    }

    @Test
    void deveLancarExcecaoAoAbrirSessaoParaPautaInexistente() {
        UUID pautaId = pauta.getId();
        when(pautaRepo.findById(pautaId)).thenReturn(Optional.empty());

        assertThrows(VotosException.class, () -> votacaoService.abrirSessao(pautaId, 10));
    }

    @Test
    public void deveBuscarSessaoComSucesso() {
        UUID pautaId = UUID.randomUUID();
        SessaoVotacao sessao = new SessaoVotacao();

        when(valueOperations.get("sessao:" + pautaId)).thenReturn(sessao);

        SessaoVotacao resultado = votacaoService.buscarSessao(pautaId);

        assertNotNull(resultado);
        verify(valueOperations, times(1)).get("sessao:" + pautaId);
    }


    @Test
    void deveFecharSessaoComSucesso() {
        UUID pautaId = pauta.getId();
        String key = "sessao:" + pautaId;

        votacaoService.fecharSessao(pautaId);

        verify(redisTemplate).delete(key);
    }

    @Test
    void deveRegistrarVotoComSucesso() {
        Associado associado = new Associado();
        associado.setId(votoDTO.getAssociadoId());
        associado.setCpf("valid-cpf");

        when(associadoRepo.findById(votoDTO.getAssociadoId())).thenReturn(Optional.of(associado));
        when(pautaRepo.findById(votoDTO.getPautaId())).thenReturn(Optional.of(pauta));
        when(redisTemplate.opsForValue().get("sessao:" + pauta.getId())).thenReturn(new SessaoVotacao());
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        when(cpfValidationService.cpfValido(anyString())).thenReturn(true);

        when(votoRepo.save(any(Voto.class))).thenReturn(new Voto());

        Voto resultado = votacaoService.registrarVoto(votoDTO);

        assertNotNull(resultado);
        verify(redisTemplate.opsForValue()).set(anyString(), any(SessaoVotacao.class));
    }

    @Test
    void deveLancarExcecaoAoRegistrarVotoParaAssociadoInexistente() {
        when(associadoRepo.findById(votoDTO.getAssociadoId())).thenReturn(Optional.empty());

        assertThrows(VotosException.class, () -> votacaoService.registrarVoto(votoDTO));
    }

    @Test
    void deveLancarExcecaoAoRegistrarVotoParaPautaInexistente() {
        when(associadoRepo.findById(votoDTO.getAssociadoId())).thenReturn(Optional.of(associado));
        when(pautaRepo.findById(votoDTO.getPautaId())).thenReturn(Optional.empty());

        assertThrows(VotosException.class, () -> votacaoService.registrarVoto(votoDTO));
    }

    @Test
    void deveLancarExcecaoAoRegistrarVotoParaSessaoFechada() {
        when(associadoRepo.findById(votoDTO.getAssociadoId())).thenReturn(Optional.of(associado));
        when(pautaRepo.findById(votoDTO.getPautaId())).thenReturn(Optional.of(pauta));
        when(redisTemplate.opsForValue().get("sessao:" + pauta.getId())).thenReturn(null);

        assertThrows(VotosException.class, () -> votacaoService.registrarVoto(votoDTO));
    }

    @Test
    void deveCalcularResultadoComSucesso() {
        Pauta pautaMock = new Pauta();
        pautaMock.setId(pauta.getId());
        pautaMock.setDescricao("Descrição da Pauta");

        when(pautaRepo.findById(pauta.getId())).thenReturn(Optional.of(pautaMock));

        when(votoRepo.findByPautaId(pauta.getId())).thenReturn(Collections.singletonList(new Voto(null, pautaMock, associado, true)));

        ResultadoVotacaoDTO resultado = votacaoService.calcularResultado(pauta.getId());

        assertNotNull(resultado);
        assertEquals(pautaMock.getDescricao(), resultado.getDescricaoPauta());
    }

    @Test
    void devePublicarResultadoComSucesso() {
        when(pautaRepo.findById(pauta.getId())).thenReturn(Optional.of(pauta));

        when(votoRepo.findByPautaId(pauta.getId())).thenReturn(Collections.singletonList(new Voto(null, pauta, associado, true)));

        CompletableFuture<SendResult<String, String>> futuroMock = CompletableFuture.completedFuture(new SendResult<>(null, null));
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(futuroMock);

        votacaoService.publicarResultado(pauta.getId());

        verify(kafkaTemplate).send(anyString(), anyString());
    }


    @Test
    void deveLancarExcecaoAoCalcularResultadoParaPautaInexistente() {
        when(pautaRepo.findById(pauta.getId())).thenReturn(Optional.empty());

        assertThrows(VotosException.class, () -> votacaoService.calcularResultado(pauta.getId()));
    }
}
