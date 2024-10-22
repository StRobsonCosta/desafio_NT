package br.com.desafio.voto.exception;

import br.com.desafio.voto.enums.ErroMensagem;

public class VotosException extends RuntimeException {

    public VotosException(ErroMensagem mensagem) {
        super(mensagem.getMensagem());
    }

    public VotosException(ErroMensagem mensagem, Throwable cause) {
        super(mensagem.getMensagem(), cause);
    }
}
