package br.com.desafio.voto.repository;

import br.com.desafio.voto.model.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, UUID> {

    Boolean existsByPautaId(UUID pautaID);
}
