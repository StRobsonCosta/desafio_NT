package br.com.desafio.voto.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class PautaDTO {

    @NotBlank(message = "A Descrição da Pauta é Obrigatória!")
    private String descricao;
}
