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
import br.com.desafio.voto.repository.SessaoVotacaoRepository;
import br.com.desafio.voto.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepo;
    private final PautaRepository pautaRepo;
    private final VotoRepository votoRepo;

    private final AssociadoRepository associadoRepo;
    private final CpfValidationService cpfValidationService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public SessaoVotacao abrirSessao(UUID pautaId, long minutos) {
        Pauta pauta = pautaRepo.findById(pautaId)
                .orElseThrow(() -> new VotosException(ErroMensagem.PAUTA_NAO_ENCONTRADA));

        if (sessaoVotacaoRepo.existsByPautaId(pautaId))
            throw new VotosException(ErroMensagem.SESSAO_JA_ABERTA);


        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime fim = agora.plusMinutes(minutos);

        SessaoVotacao sessao = new SessaoVotacao(null, pauta, agora, fim);
        return sessaoVotacaoRepo.save(sessao);
    }

    @Transactional
    public Voto registrarVoto(VotoDTO votoDTO) {

        Associado associado = associadoRepo.findById(votoDTO.getAssociadoId())
                .orElseThrow(() -> new VotosException(ErroMensagem.ASSOCIADO_NAO_ENCONTRADO ));

        validarCpf(associado.getCpf());

        Pauta pauta = pautaRepo.findById(votoDTO.getPautaId())
                .orElseThrow(() ->  new VotosException(ErroMensagem.PAUTA_NAO_ENCONTRADA));

        SessaoVotacao sessao = sessaoVotacaoRepo.findByPautaId(votoDTO.getPautaId())
                .orElseThrow(() -> new VotosException(ErroMensagem.SESSAO_ENCERRADA));

        validarSessaoAberta(sessao);

        // Verifica se o carinha já votou na Pauta
        if (votoRepo.existsByPautaIdAndAssociadoId(pauta.getId(), associado.getId()))
            throw new VotosException(ErroMensagem.ASSOCIADO_JA_VOTOU);

        // Registra o voto
        Voto voto = new Voto(null, pauta, associado, votoDTO.getValorVoto());
        return votoRepo.save(voto);
    }

    private void validarCpf(String cpf) {
        Boolean aptoParaVotar = cpfValidationService.isCpfValid(cpf);
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
    }

    private void validarSessaoAberta(SessaoVotacao sessao) {
        if (sessao.getFim().isBefore(LocalDateTime.now()))
            throw new VotosException(ErroMensagem.SESSAO_ENCERRADA);

    }

}

