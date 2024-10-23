package br.com.desafio.voto.config;

import br.com.desafio.voto.enums.ErroMensagem;
import br.com.desafio.voto.exception.VotosException;
import br.com.desafio.voto.service.VotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisExpirationListener implements MessageListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final VotacaoService votacaoService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = new String(message.getBody());
        String pautaIdStr = key.split(":")[1];

        try {
            UUID pautaId = UUID.fromString(pautaIdStr);
            votacaoService.publicarResultado(pautaId);
        } catch (Exception e) {
            throw new VotosException(ErroMensagem.SESSAO_JA_ABERTA, e);
        }
    }
}
