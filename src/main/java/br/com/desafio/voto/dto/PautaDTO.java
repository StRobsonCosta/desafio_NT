package br.com.desafio.voto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PautaDTO {

    private UUID id;

    @NotBlank(message = "A Descrição da Pauta é Obrigatória!")
    private String descricao;
}
