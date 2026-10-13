package basquat.estacionamento.infrastructure.persistence.repository;

import basquat.estacionamento.infrastructure.persistence.entity.BlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório JPA para BlacklistEntity.
 * Fica em infrastructure.persistence.repository.
 */
public interface BlacklistJPARepository extends JpaRepository<BlacklistEntity, String> {
    List<BlacklistEntity> findAllByOrderByCriadoEmDesc();
}