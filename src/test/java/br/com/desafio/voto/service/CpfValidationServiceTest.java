package br.com.desafio.voto.service;

import br.com.desafio.voto.dto.CpfStatusDto;
import br.com.desafio.voto.enums.StatusVoto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = "cpf.validation.url=https://user-info.herokuapp.com/users/{cpf}")
public class CpfValidationServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private CpfValidationService service;

    @Test
    void testCpfValid() {
        when(restTemplate.getForObject("https://user-info.herkuapp.com/users/12345678900", CpfStatusDto.class))
                .thenReturn(new CpfStatusDto(StatusVoto.ABLE_TO_VOTE));

        assertTrue(service.isCpfValid("12345678900"));
    }

    @Test
    void testCpfInvalid() {
        when(restTemplate.getForObject("https://user-info.herkuapp.com/users/12345678900", CpfStatusDto.class))
                .thenReturn(new CpfStatusDto(StatusVoto.UNABLE_TO_VOTE));

        assertFalse(service.isCpfValid("12345678900"));
    }
}
