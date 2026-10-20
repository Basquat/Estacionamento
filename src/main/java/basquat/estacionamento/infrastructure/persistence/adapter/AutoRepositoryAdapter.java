package basquat.estacionamento.infrastructure.persistence.adapter;

import basquat.estacionamento.application.port.out.AutoRepositoryPort;
import basquat.estacionamento.domain.model.Auto;
import basquat.estacionamento.domain.model.Pagamento;
import basquat.estacionamento.infrastructure.persistence.entity.AutoEntity;
import basquat.estacionamento.infrastructure.persistence.entity.PagamentoEntity;
import basquat.estacionamento.infrastructure.persistence.repository.AutoJPARepository;
import basquat.estacionamento.infrastructure.persistence.repository.PagamentoJPARepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter que implementa AutoRepositoryPort usando JPA.
 * Fica em infrastructure.persistence.adapter.
 */
@Component
public class AutoRepositoryAdapter implements AutoRepositoryPort {

    private final AutoJPARepository jpa;
    private final PagamentoJPARepository pagamentoJPA;

    public AutoRepositoryAdapter(AutoJPARepository jpa, PagamentoJPARepository pagamentoJPA) {
        this.jpa = jpa;
        this.pagamentoJPA = pagamentoJPA;
    }

    @Override
    public List<Auto> findAll() {
        return jpa.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Auto> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByPlacaIgnoreCase(String placa) {
        return jpa.existsByPlacaIgnoreCase(placa);
    }

    @Override
    @Transactional
    public Auto save(Auto auto) {
        AutoEntity entity = toEntity(auto);
        AutoEntity salvo = jpa.save(entity);
        return toDomain(salvo);
    }

    @Override
    @Transactional
    public int deleteByIdReturningCount(String id) {
        pagamentoJPA.deleteByAutoId(id);
        return jpa.deleteByIdReturningCount(id);
    }

    // Converte Entity -> Domain
    private Auto toDomain(AutoEntity e) {
        List<Pagamento> pagamentos = new ArrayList<>();
        if (e.getPagamentos() != null) {
            for (PagamentoEntity pe : e.getPagamentos()) {
                pagamentos.add(toPagamentoDomain(pe));
            }
        }
        Auto auto = new Auto(
                e.getId(),
                e.getPlaca(),
                e.getTipo(),
                e.getValor(),
                e.getPago(),
                e.getEntrada(),
                pagamentos
        );
        auto.setValorPrevisto(e.getValorPrevisto());
        auto.setFoto(e.getFoto());
        auto.setSaida(e.getSaida());
        return auto;
    }

    private Pagamento toPagamentoDomain(PagamentoEntity pe) {
        return new Pagamento(
                pe.getId(),
                pe.getAutoId(),
                pe.getMetodoPagamento(),
                pe.getValor(),
                pe.getTrocoDinheiro(),
                pe.getTrocoPix(),
                pe.getData()
        );
    }

    // Converte Domain -> Entity
    private AutoEntity toEntity(Auto a) {
        AutoEntity e = new AutoEntity();
        e.setId(a.getId());
        e.setPlaca(a.getPlaca());
        e.setTipo(a.getTipo());
        e.setValor(a.getValor());
        e.setValorPrevisto(a.getValorPrevisto());
        e.setPago(a.getPago());
        e.setEntrada(a.getEntrada());
        e.setFoto(a.getFoto());
        e.setSaida(a.getSaida());

        List<PagamentoEntity> pagamentos = new ArrayList<>();
        if (a.getPagamentos() != null) {
            for (Pagamento p : a.getPagamentos()) {
                PagamentoEntity pe = toPagamentoEntity(p);
                pe.setAutoId(a.getId());
                pagamentos.add(pe);
            }
        }
        e.setPagamentos(pagamentos);
        return e;
    }

    private PagamentoEntity toPagamentoEntity(Pagamento p) {
        PagamentoEntity pe = new PagamentoEntity();
        if (p.getId() == null || p.getId().isBlank()) {
            pe.setId(UUID.randomUUID().toString());
        } else {
            pe.setId(p.getId());
        }
        pe.setAutoId(p.getAutoId());
        pe.setMetodoPagamento(p.getMetodoPagamento());
        pe.setValor(p.getValor());
        pe.setTrocoDinheiro(p.getTrocoDinheiro());
        pe.setTrocoPix(p.getTrocoPix());
        pe.setData(p.getData());
        return pe;
    }
}