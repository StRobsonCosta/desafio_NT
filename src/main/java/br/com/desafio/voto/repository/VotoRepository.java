package br.com.desafio.voto.repository;

import br.com.desafio.voto.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VotoRepository extends JpaRepository<Voto, UUID> {

    Boolean existsByPautaIdAndAssociadoId(UUID pautaId, UUID associadoId); //validar se associado já votou na pauta RN3

    List<Voto> findByPautaId(UUID pautaId); //lista todos os votos para RN4
}
