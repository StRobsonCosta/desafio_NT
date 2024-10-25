package br.com.desafio.voto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerConfig {

    @Autowired
    private JavaMailSender mailSender;
    private static final String MAIL_NO_REPLY = "noreply@desafio.nt";

    @Value("${spring.mail.username}")
    private String mailUsername;

    @KafkaListener(topics = "votacao_resultados", groupId = "grupo-votacao")
    public void consumirAlerta(String mensagem) {

        System.out.println("Alerta recebido: " + mensagem);

        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(mailUsername);
            email.setFrom(MAIL_NO_REPLY);
            email.setSubject("Resultado da Votação");
            email.setText("Alerta importante: " + mensagem);

            mailSender.send(email);

            final String formatted = """
                    E-mail enviado para %s com a mensagem: %s
                    """.formatted(mailUsername, mensagem);
            System.out.println(formatted);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }
    }
}
