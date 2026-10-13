package basquat.estacionamento.infrastructure.persistence.adapter;

import basquat.estacionamento.application.port.out.BlacklistRepositoryPort;
import basquat.estacionamento.domain.model.BlacklistEntry;
import basquat.estacionamento.infrastructure.persistence.entity.BlacklistEntity;
import basquat.estacionamento.infrastructure.persistence.repository.BlacklistJPARepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter que implementa BlacklistRepositoryPort usando JPA.
 * Fica em infrastructure.persistence.adapter.
 */
@Component
public class BlacklistRepositoryAdapter implements BlacklistRepositoryPort {

    private final BlacklistJPARepository jpa;

    public BlacklistRepositoryAdapter(BlacklistJPARepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<BlacklistEntry> findAllByCriadoEmDesc() {
        return jpa.findAllByOrderByCriadoEmDesc().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BlacklistEntry> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsById(String id) {
        return jpa.existsById(id);
    }

    @Override
    @Transactional
    public BlacklistEntry save(BlacklistEntry entry) {
        BlacklistEntity entity = toEntity(entry);
        BlacklistEntity salvo = jpa.save(entity);
        return toDomain(salvo);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        jpa.deleteById(id);
    }

    private BlacklistEntry toDomain(BlacklistEntity e) {
        return new BlacklistEntry(
                e.getId(),
                e.getPlaca(),
                e.getMotivo(),
                e.getFoto(),
                e.getCriadoEm()
        );
    }

    private BlacklistEntity toEntity(BlacklistEntry entry) {
        BlacklistEntity e = new BlacklistEntity();
        if (entry.getId() == null || entry.getId().isBlank()) {
            e.setId(UUID.randomUUID().toString());
        } else {
            e.setId(entry.getId());
        }
        e.setPlaca(entry.getPlaca());
        e.setMotivo(entry.getMotivo());
        e.setFoto(entry.getFoto());
        e.setCriadoEm(entry.getCriadoEm());
        return e;
    }
}