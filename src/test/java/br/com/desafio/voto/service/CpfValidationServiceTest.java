package br.com.desafio.voto.service;

import br.com.caelum.stella.ValidationMessage;
import br.com.caelum.stella.validation.CPFValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CpfValidationServiceTest {

    @InjectMocks
    private CpfValidationService cpfValidationService;

    @Mock
    private CPFValidator cpfValidator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveRetornarTrueQuandoCpfValido() {
        String cpfValido = "12345678909"; // CPF válido fictício
        when(cpfValidator.invalidMessagesFor(cpfValido)).thenReturn(Collections.emptyList());

        Boolean resultado = cpfValidationService.cpfValido(cpfValido);

        assertTrue(resultado);
        verify(cpfValidator, times(1)).invalidMessagesFor(cpfValido);
    }

    @Test
    void deveRetornarFalseQuandoCpfInvalido() {
        String cpfInvalido = "12345678900"; // CPF inválido fictício
        List<ValidationMessage> erros = List.of(mock(ValidationMessage.class));
        when(cpfValidator.invalidMessagesFor(cpfInvalido)).thenReturn(erros);

        Boolean resultado = cpfValidationService.cpfValido(cpfInvalido);

        assertFalse(resultado);
        verify(cpfValidator, times(1)).invalidMessagesFor(cpfInvalido);
    }

    @Test
    void deveRetornarFalseQuandoCpfNuloOuVazio() {
        String cpfNulo = null;
        String cpfVazio = "";

        when(cpfValidator.invalidMessagesFor(anyString())).thenReturn(List.of(mock(ValidationMessage.class)));

        assertFalse(cpfValidationService.cpfValido(cpfNulo));
        assertFalse(cpfValidationService.cpfValido(cpfVazio));
    }
}
