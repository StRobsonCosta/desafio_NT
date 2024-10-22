package br.com.desafio.voto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VotoDTO {

    @NotNull(message = "O ID do Associado é Obrigatório!")
    private UUID associadoId;

    @NotNull(message = "O ID da Pauta é Obrigatório!")
    private UUID pautaId;

    @NotNull(message = "O Voto é Obrigatório!")
    private Boolean valorVoto;
}
