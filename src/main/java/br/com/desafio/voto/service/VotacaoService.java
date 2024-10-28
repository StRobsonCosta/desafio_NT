package br.com.desafio.voto.service;

import br.com.desafio.voto.dto.ResultadoVotacaoDTO;
import br.com.desafio.voto.dto.VotoDTO;
import br.com.desafio.voto.enums.ErroMensagem;
import br.com.desafio.voto.exception.VotosException;
import br.com.desafio.voto.model.Associado;
import br.com.desafio.voto.model.Pauta;
import br.com.desafio.voto.model.SessaoVotacao;
import br.com.desafio.voto.model.Voto;
import br.com.desafio.voto.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class VotacaoService {

    private final VotoRepository votoRepo;

    private final PautaService pautaService;
    private final AssociadoService associadoService;
    private final CpfValidationService cpfValidationService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, SessaoVotacao> redisTemplate;

    public void abrirSessao(UUID pautaId, long minutos) {

        String key = "sessao:" + pautaId;
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime fim = agora.plusMinutes(minutos);

        Pauta pauta = pautaService.buscarPauta(pautaId);

        SessaoVotacao sessao = new SessaoVotacao(null, new Pauta(pautaId, pauta.getDescricao()), agora, fim);

        redisTemplate.opsForValue().set(key, sessao, minutos, TimeUnit.MINUTES);
    }

    public SessaoVotacao buscarSessao(UUID pautaId) {
        String key = "sessao:" + pautaId;
        return redisTemplate.opsForValue().get(key);
    }

    public void fecharSessao(UUID pautaId) {
        String key = "sessao:" + pautaId;
        redisTemplate.delete(key);
        log.info("Sessão Encerrada");
    }

    @Transactional
    public Voto registrarVoto(VotoDTO votoDTO) {

        Associado associado = associadoService.buscarAssociado(votoDTO.getAssociadoId());
        validarCpf(associado.getCpf());
        Pauta pauta = pautaService.buscarPauta(votoDTO.getPautaId());
        SessaoVotacao sessao = buscarSessao(votoDTO.getPautaId());

        validarSessaoAberta(sessao);
        validarVotoUnico(pauta, associado);

        Voto voto = new Voto(null, pauta, associado, votoDTO.getValorVoto());
        Voto savedVoto = votoRepo.save(voto);

        registrarVotoNoRedis(pauta, associado, sessao);

        return savedVoto;
    }

    private void validarVotoUnico(Pauta pauta, Associado associado) {
        String votoKey = String.format("voto:%s:%s", pauta.getId(), associado.getId());
        if (Boolean.TRUE.equals(redisTemplate.hasKey(votoKey))) {
            throw new VotosException(ErroMensagem.ASSOCIADO_JA_VOTOU);
        }
    }

    private void registrarVotoNoRedis(Pauta pauta, Associado associado, SessaoVotacao sessao) {
        String votoKey = String.format("voto:%s:%s", pauta.getId(), associado.getId());
        redisTemplate.opsForValue().set(votoKey, sessao);
    }

    private void validarCpf(String cpf) {
        Boolean aptoParaVotar = cpfValidationService.cpfValido(cpf);
        if (Boolean.FALSE.equals(aptoParaVotar))
            throw new VotosException(ErroMensagem.ASSOCIADO_INAPTO);

    }

    public ResultadoVotacaoDTO calcularResultado(UUID pautaId) {
        List<Voto> votos = votoRepo.findByPautaId(pautaId);
        Long votosSim = votos.stream().filter(Voto::getValorVoto).count();
        Long votosNao = votos.size() - votosSim;

        Pauta pauta = pautaService.buscarPauta(pautaId);

        return new ResultadoVotacaoDTO(pauta.getDescricao(), votosSim, votosNao);
    }

    public void publicarResultado(UUID pautaId) {
        ResultadoVotacaoDTO resultado = calcularResultado(pautaId);
        String mensagem = String.format("Resultado da Pauta '%s': Sim=%d, Não=%d",
                resultado.getDescricaoPauta(), resultado.getVotosSim(), resultado.getVotosNao());

        kafkaTemplate.send("votacao_resultados", mensagem);

        log.info("Mensagem enviada para o Kafka: {}", mensagem);
    }

    private void validarSessaoAberta(SessaoVotacao sessao) {
        if (Objects.isNull(sessao))
            throw new VotosException(ErroMensagem.SESSAO_ENCERRADA);

    }

}

