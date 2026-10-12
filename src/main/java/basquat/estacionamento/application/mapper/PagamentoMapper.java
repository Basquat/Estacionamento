package basquat.estacionamento.application.mapper;

import basquat.estacionamento.domain.model.Pagamento;
import basquat.estacionamento.application.dto.request.PagamentoRequest;
import basquat.estacionamento.application.dto.response.PagamentoResponse;

public class PagamentoMapper {

    public static Pagamento toEntity(PagamentoRequest request) {
        if (request == null) return null;

        Pagamento p = new Pagamento();
        p.setId(java.util.UUID.randomUUID().toString());
        p.setMetodoPagamento(request.getMetodo());
        p.setValor(request.getValor());
        p.setTroco(request.getTroco());
        return p;
    }

    public static PagamentoResponse toResponse(Pagamento entity) {
        if (entity == null) return null;

        return new PagamentoResponse(
            entity.getId(),
            entity.getMetodoPagamento(),
            entity.getValor(),
            entity.getTroco(),
            entity.getData()
        );
    }
}