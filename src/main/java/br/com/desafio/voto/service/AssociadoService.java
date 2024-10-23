package br.com.desafio.voto.service;

import br.com.desafio.voto.dto.AssociadoDto;
import br.com.desafio.voto.enums.ErroMensagem;
import br.com.desafio.voto.exception.VotosException;
import br.com.desafio.voto.model.Associado;
import br.com.desafio.voto.repository.AssociadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssociadoService {

    private final AssociadoRepository associadoRepo;
    private final CpfValidationService cpfValidator;


    public AssociadoDto criarAssociado(AssociadoDto dto) {
        validarCpfAssociado(dto);

        Associado associado = new Associado();
        associado.setNome(dto.getNome());
        associado.setCpf(dto.getCpf());

        try {
            Associado novoAssociado = associadoRepo.save(associado);
            return new AssociadoDto(novoAssociado.getId(), novoAssociado.getNome(), novoAssociado.getCpf());
        } catch (Exception e) {
            throw new VotosException(ErroMensagem.ERRO_CRIAR_ASSOCIADO, e);
        }
    }

    protected void validarCpfAssociado(AssociadoDto dto) {
        if (Objects.isNull(dto.getCpf()))
            throw new VotosException(ErroMensagem.CPF_NAO_ENCONTRADO);

        Boolean cpfValido = cpfValidator.cpfValido(dto.getCpf());

        if (Boolean.FALSE.equals(cpfValido))
            throw new VotosException(ErroMensagem.CPF_INVALIDO);

        Boolean cpfDuplicado = associadoRepo.existsByCpf(dto.getCpf());

        if (Boolean.TRUE.equals(cpfDuplicado))
            throw new VotosException(ErroMensagem.CPF_JA_CADASTRADO);

    }

    public AssociadoDto buscarAssociado(UUID associadoId) {
        Associado associado = associadoRepo.findById(associadoId)
                .orElseThrow(() -> new VotosException(ErroMensagem.ASSOCIADO_NAO_ENCONTRADO));

        return new AssociadoDto(associado.getId(), associado.getNome(), associado.getCpf());
    }

    public List<AssociadoDto> listarAssocidados() {
        List<Associado>  associados = associadoRepo.findAll();

        if (CollectionUtils.isEmpty(associados))
            throw new VotosException(ErroMensagem.ASSOCIADO_NAO_ENCONTRADO);

        return associados.stream()
                .map(associado -> new AssociadoDto(associado.getId(), associado.getNome(), associado.getCpf()))
                .collect(Collectors.toList());
    }

    public void deletarAssociado(UUID associadoId) {
        if (Objects.isNull(associadoId))
            throw new VotosException(ErroMensagem.ASSOCIADO_NAO_ENCONTRADO);

        associadoRepo.deleteById(associadoId);
    }
}
