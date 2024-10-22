package br.com.desafio.voto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @ManyToOne
    @JoinColumn(name = "associado_id", nullable = false)
    private Associado associado;

    @Column(nullable = false)
    private Boolean valorVoto;  // talvez não seja um bom nome para esse atributo

}
