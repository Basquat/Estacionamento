package basquat.estacionamento.infrastructure.persistence.repository;

import basquat.estacionamento.infrastructure.persistence.entity.AutoEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para AutoEntity.
 * Fica em infrastructure.persistence.repository.
 */
public interface AutoJPARepository extends JpaRepository<AutoEntity, String> {
    boolean existsByPlacaIgnoreCase(String placa);

    @Modifying
    @Query(value = "DELETE FROM auto_model WHERE id = :id", nativeQuery = true)
    int deleteByIdReturningCount(@Param("id") String id);
}