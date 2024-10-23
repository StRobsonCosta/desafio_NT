package br.com.desafio.voto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssociadoDto {

    private UUID id;

    private String nome;

    @NotNull(message = "O CPF é Obrigatório!")
    private String cpf;
}
