package br.com.desafio.voto.service;

import br.com.caelum.stella.ValidationMessage;
import br.com.caelum.stella.validation.CPFValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CpfValidationService {

    @Autowired
    private final CPFValidator cpfValidator;

    public Boolean cpfValido(String cpf) {
        if (Objects.isNull(cpf) || cpf.isEmpty())
            return Boolean.FALSE;

        List<ValidationMessage> erros = cpfValidator.invalidMessagesFor(cpf);

        if (!CollectionUtils.isEmpty(erros)) {
            System.out.println(erros);
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }
}
