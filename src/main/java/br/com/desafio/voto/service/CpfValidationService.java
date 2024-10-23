package br.com.desafio.voto.service;

import br.com.caelum.stella.ValidationMessage;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.desafio.voto.dto.CpfStatusDto;
import br.com.desafio.voto.enums.ErroMensagem;
import br.com.desafio.voto.enums.StatusVoto;
import br.com.desafio.voto.exception.VotosException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CpfValidationService {

    private final RestTemplate restTemplate;

    @Value("${cpf.validation.url}")
    private String cpfValidationUrl;

    public Boolean isCpfValid(String cpf) {
        try {
            String url = cpfValidationUrl.replace("{cpf}", cpf);
            CpfStatusDto response = restTemplate.getForObject(url, CpfStatusDto.class);

            return Objects.nonNull(response) && response.getStatus().equals(StatusVoto.ABLE_TO_VOTE);

        } catch (HttpClientErrorException.NotFound e) {
            throw new VotosException(ErroMensagem.CPF_NAO_ENCONTRADO);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new VotosException(ErroMensagem.CPF_INVALIDO);
            }
            throw new VotosException(ErroMensagem.ERRO_VALIDAR_CPF, e);
        }
    }

    public Boolean cpfValido(String cpf) {
        CPFValidator cpfValidator = new CPFValidator();
        List<ValidationMessage> erros = cpfValidator.invalidMessagesFor(cpf);

        if (!CollectionUtils.isEmpty(erros)) {
            System.out.println(erros);
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }
}
