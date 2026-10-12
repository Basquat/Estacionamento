package basquat.estacionamento.infrastructure.persistence.repository;

import basquat.estacionamento.infrastructure.persistence.entity.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para PagamentoEntity.
 * Fica em infrastructure.persistence.repository.
 */
public interface PagamentoJPARepository extends JpaRepository<PagamentoEntity, String> {
    void deleteByAutoId(String autoId);
}