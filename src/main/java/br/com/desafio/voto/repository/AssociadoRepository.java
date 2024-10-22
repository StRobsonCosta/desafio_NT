package br.com.desafio.voto.repository;

import br.com.desafio.voto.model.Associado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssociadoRepository extends JpaRepository<Associado, UUID> {

    Boolean existsByCpf(String cpf);
}
