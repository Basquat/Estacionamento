package basquat.estacionamento.application.port.out;

import basquat.estacionamento.domain.model.BlacklistEntry;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída - Repositório de Blacklist.
 * Quem implementa fica em infrastructure.
 */
public interface BlacklistRepositoryPort {
    List<BlacklistEntry> findAllByCriadoEmDesc();
    Optional<BlacklistEntry> findById(String id);
    boolean existsById(String id);
    BlacklistEntry save(BlacklistEntry entry);
    void deleteById(String id);
}