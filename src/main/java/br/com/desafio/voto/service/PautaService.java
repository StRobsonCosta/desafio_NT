package br.com.desafio.voto.service;

import br.com.desafio.voto.dto.PautaDTO;
import br.com.desafio.voto.enums.ErroMensagem;
import br.com.desafio.voto.exception.VotosException;
import br.com.desafio.voto.model.Pauta;
import br.com.desafio.voto.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepo;

    public PautaDTO criarPauta(PautaDTO pautaDTO) {
        if (Objects.isNull(pautaDTO))
            throw new VotosException(ErroMensagem.PAUTA_INVALIDA);
        if (!StringUtils.hasText(pautaDTO.getDescricao()))
            throw new VotosException(ErroMensagem.DESCRICAO_INVALIDA);

        Pauta pauta = new Pauta();
        pauta.setDescricao(pautaDTO.getDescricao());

        try {
            Pauta novaPauta = pautaRepo.save(pauta);
            return new PautaDTO(novaPauta.getId(), novaPauta.getDescricao());
        } catch (Exception e) {
            throw new VotosException(ErroMensagem.ERRO_SALVAR_PAUTA, e);
        }
    }

    public PautaDTO buscarPautaDto(UUID pautaId) {
        Pauta pauta = pautaRepo.findById(pautaId)
                .orElseThrow(() -> new VotosException(ErroMensagem.PAUTA_NAO_ENCONTRADA));

        return new PautaDTO(pauta.getId(), pauta.getDescricao());
    }

    public Pauta buscarPauta(UUID pautaId) {
        return pautaRepo.findById(pautaId)
                .orElseThrow(() -> new VotosException(ErroMensagem.PAUTA_NAO_ENCONTRADA));
    }

    public List<PautaDTO> listarPautasDto() {
        List<Pauta> pautas = pautaRepo.findAll();

        if (CollectionUtils.isEmpty(pautas))
            throw new VotosException(ErroMensagem.PAUTA_NAO_ENCONTRADA);

        return pautas.stream()
                .map(pauta -> new PautaDTO(pauta.getId(), pauta.getDescricao()))
                .collect(Collectors.toList());
    }

    public void deletarPauta(UUID pautaId) {
        if (Objects.isNull(pautaId))
            throw new VotosException(ErroMensagem.PAUTA_NAO_ENCONTRADA);

        pautaRepo.deleteById(pautaId);
    }
}
