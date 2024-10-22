package br.com.desafio.voto.dto;

import br.com.desafio.voto.enums.StatusVoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CpfStatusDto {
    private StatusVoto status;
}
