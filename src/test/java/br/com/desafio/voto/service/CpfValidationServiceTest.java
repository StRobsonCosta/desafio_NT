package br.com.desafio.voto.service;

import br.com.caelum.stella.ValidationMessage;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.desafio.voto.dto.CpfStatusDto;
import br.com.desafio.voto.enums.StatusVoto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

//@SpringBootTest
//@TestPropertySource(properties = "cpf.validation.url=https://user-info.herokuapp.com/users/{cpf}")
public class CpfValidationServiceTest {

//    @MockBean
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private CpfValidationService service;

    @InjectMocks
    private CpfValidationService cpfValidationService;

    @Mock
    private CPFValidator cpfValidator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void testCpfInvalid() {
//        when(restTemplate.getForObject("https://user-info.herkuapp.com/users/12345678900", CpfStatusDto.class))
//                .thenReturn(new CpfStatusDto(StatusVoto.UNABLE_TO_VOTE));
//
//        assertFalse(service.isCpfValid("12345678900"));
//    }


    @Test
    void deveRetornarTrueQuandoCpfValido() {
        // Arrange
        String cpfValido = "12345678909"; // CPF válido fictício
        when(cpfValidator.invalidMessagesFor(cpfValido)).thenReturn(Collections.emptyList());

        // Act
        Boolean resultado = cpfValidationService.cpfValido(cpfValido);

        // Assert
        assertTrue(resultado);
        verify(cpfValidator, times(1)).invalidMessagesFor(cpfValido);
    }

    @Test
    void deveRetornarFalseQuandoCpfInvalido() {
        // Arrange
        String cpfInvalido = "12345678900"; // CPF inválido fictício
        List<ValidationMessage> erros = List.of(mock(ValidationMessage.class));
        when(cpfValidator.invalidMessagesFor(cpfInvalido)).thenReturn(erros);

        // Act
        Boolean resultado = cpfValidationService.cpfValido(cpfInvalido);

        // Assert
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
