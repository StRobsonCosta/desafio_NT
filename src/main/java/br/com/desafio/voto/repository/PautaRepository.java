package br.com.desafio.voto.repository;

import br.com.desafio.voto.model.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, UUID> {
}
