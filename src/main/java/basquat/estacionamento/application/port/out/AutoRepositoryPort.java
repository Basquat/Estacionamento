package basquat.estacionamento.application.port.out;

import basquat.estacionamento.domain.model.Auto;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída - Repositório de Auto.
 * Quem implementa fica em infrastructure.
 */
public interface AutoRepositoryPort {
    List<Auto> findAll();
    Optional<Auto> findById(String id);
    boolean existsByPlacaIgnoreCase(String placa);
    Auto save(Auto auto);
    int deleteByIdReturningCount(String id);
}