package basquat.estacionamento.infrastructure.persistence.adapter;

import basquat.estacionamento.application.port.out.PagamentoRepositoryPort;
import basquat.estacionamento.infrastructure.persistence.repository.PagamentoJPARepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter que implementa PagamentoRepositoryPort usando JPA.
 * Fica em infrastructure.persistence.adapter.
 */
@Component
public class PagamentoRepositoryAdapter implements PagamentoRepositoryPort {

    private final PagamentoJPARepository jpa;

    public PagamentoRepositoryAdapter(PagamentoJPARepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public void deleteByAutoId(String autoId) {
        jpa.deleteByAutoId(autoId);
    }
}