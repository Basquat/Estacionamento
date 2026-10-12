package basquat.estacionamento.application.mapper;

import basquat.estacionamento.User.AutoModel;
import basquat.estacionamento.User.Pagamento;
import basquat.estacionamento.application.dto.request.AutoCreateRequest;
import basquat.estacionamento.application.dto.request.AutoUpdateRequest;
import basquat.estacionamento.application.dto.request.PagamentoRequest;
import basquat.estacionamento.application.dto.response.AutoResponse;
import basquat.estacionamento.application.dto.response.PagamentoResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Mapper {

    public static AutoModel toEntity(AutoCreateRequest request) {
        if (request == null) return null;

        AutoModel model = new AutoModel();
        model.setId(UUID.randomUUID().toString());
        model.setPlaca(request.getPlaca());
        model.setTipo(request.getTipo());
        model.setValor(request.getValor());
        model.setPago(false);
        model.setEntrada(System.currentTimeMillis());

        if (request.getPagamentos() != null) {
            List<Pagamento> pagamentos = new ArrayList<>();
            for (PagamentoRequest pr : request.getPagamentos()) {
                Pagamento p = toPagamentoEntity(pr);
                p.setAutoId(model.getId());
                pagamentos.add(p);
            }
            model.setPagamentos(pagamentos);
        }

        return model;
    }

    public static void updateEntity(AutoModel model, AutoUpdateRequest request) {
        if (request == null || model == null) return;

        if (request.getPlaca() != null) model.setPlaca(request.getPlaca());
        if (request.getTipo() != null) model.setTipo(request.getTipo());
        if (request.getValor() != null) model.setValor(request.getValor());
        if (request.getPago() != null) model.setPago(request.getPago());

        if (request.getPagamentos() != null) {
            model.getPagamentos().clear();
            for (PagamentoRequest pr : request.getPagamentos()) {
                Pagamento p = toPagamentoEntity(pr);
                p.setAutoId(model.getId());
                model.getPagamentos().add(p);
            }
        }
    }

    public static AutoResponse toResponse(AutoModel model) {
        if (model == null) return null;

        List<PagamentoResponse> pagamentosResponse = new ArrayList<>();
        if (model.getPagamentos() != null) {
            for (Pagamento p : model.getPagamentos()) {
                pagamentosResponse.add(toPagamentoResponse(p));
            }
        }

        return new AutoResponse(
            model.getId(),
            model.getPlaca(),
            model.getTipo(),
            model.getValor(),
            model.getPago(),
            model.getEntrada(),
            pagamentosResponse
        );
    }

    public static Pagamento toPagamentoEntity(PagamentoRequest request) {
        if (request == null) return null;

        Pagamento p = new Pagamento();
        p.setId(UUID.randomUUID().toString());
        p.setMetodoPagamento(request.getMetodo());
        p.setValor(request.getValor());
        p.setTroco(request.getTroco());
        return p;
    }

    public static PagamentoResponse toPagamentoResponse(Pagamento entity) {
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