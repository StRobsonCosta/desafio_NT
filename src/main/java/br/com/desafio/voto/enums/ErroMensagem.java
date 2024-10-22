package br.com.desafio.voto.enums;

public enum ErroMensagem {

    PAUTA_NAO_ENCONTRADA("Pauta não encontrada parao ID fornecido."),
    SESSAO_JA_ABERTA("Já existe uma sessão aberta para esta pauta."),
    ASSOCIADO_NAO_ENCONTRADO("Associado não encontrado para o ID fornecido."),
    ASSOCIADO_INAPTO("Associado não está apto para votarr."),
    CPF_INVALIDO("Formato de CPF inválido."),
    CPF_NAO_ENCONTRADO("CPF não encontrado."),
    ASSOCIADO_JA_VOTOU("Associado já votou nesta pauta."),
    SESSAO_ENCERRADA("A sessão de votação já foi encerrada."),
    ERRO_VALIDAR_CPF("Erro ao validar oCPF.");

    private final String mensagem;

    ErroMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }
}
