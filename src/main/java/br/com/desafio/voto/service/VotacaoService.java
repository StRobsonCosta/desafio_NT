package br.com.desafio.voto.service;

import br.com.desafio.voto.dto.ResultadoVotacaoDTO;
import br.com.desafio.voto.dto.VotoDTO;
import br.com.desafio.voto.enums.ErroMensagem;
import br.com.desafio.voto.exception.VotosException;
import br.com.desafio.voto.model.Associado;
import br.com.desafio.voto.model.Pauta;
import br.com.desafio.voto.model.SessaoVotacao;
import br.com.desafio.voto.model.Voto;
import br.com.desafio.voto.repository.AssociadoRepository;
import br.com.desafio.voto.repository.PautaRepository;
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

    private final PautaRepository pautaRepo;
    private final VotoRepository votoRepo;

    private final AssociadoRepository associadoRepo;
    private final CpfValidationService cpfValidationService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, SessaoVotacao> redisTemplate;

    public void abrirSessao(UUID pautaId, long minutos) {

        String key = "sessao:" + pautaId;
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime fim = agora.plusMinutes(minutos);

        Pauta pauta = pautaRepo.findById(pautaId)
                .orElseThrow(() ->  new VotosException(ErroMensagem.PAUTA_NAO_ENCONTRADA));

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

        Associado associado = associadoRepo.findById(votoDTO.getAssociadoId())
                .orElseThrow(() -> new VotosException(ErroMensagem.ASSOCIADO_NAO_ENCONTRADO ));

        validarCpf(associado.getCpf());

        Pauta pauta = pautaRepo.findById(votoDTO.getPautaId())
                .orElseThrow(() ->  new VotosException(ErroMensagem.PAUTA_NAO_ENCONTRADA));

        SessaoVotacao sessao = buscarSessao(votoDTO.getPautaId());

        validarSessaoAberta(sessao);

        String votoKey = String.format("voto:%s:%s", pauta.getId(), associado.getId());
        if (Boolean.TRUE.equals(redisTemplate.hasKey(votoKey)))
            throw new VotosException(ErroMensagem.ASSOCIADO_JA_VOTOU);

        Voto voto = new Voto(null, pauta, associado, votoDTO.getValorVoto());
        Voto savedVoto = votoRepo.save(voto);

        redisTemplate.opsForValue().set(votoKey, sessao);

        return savedVoto;
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

        Pauta pauta = pautaRepo.findById(pautaId)
                .orElseThrow(() ->  new VotosException(ErroMensagem.PAUTA_NAO_ENCONTRADA));

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

