package br.com.desafio.voto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class PautaDTO {

    @NotBlank(message = "A Descrição da Pauta é Obrigatória!")
    private String descricao;
}
