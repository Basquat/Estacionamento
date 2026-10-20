package basquat.estacionamento.application.mapper;

import basquat.estacionamento.domain.model.Auto;
import basquat.estacionamento.domain.model.Pagamento;
import basquat.estacionamento.application.dto.request.AutoCreateRequest;
import basquat.estacionamento.application.dto.request.AutoUpdateRequest;
import basquat.estacionamento.application.dto.request.PagamentoRequest;
import basquat.estacionamento.application.dto.response.AutoResponse;
import basquat.estacionamento.application.dto.response.PagamentoResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;

public class Mapper {

    public static Auto toEntity(AutoCreateRequest request) {
        if (request == null) return null;

        Auto model = new Auto();
        model.setId(UUID.randomUUID().toString());
        model.setPlaca(request.getPlaca());
        model.setTipo(request.getTipo());
        model.setValor(request.getValor());
        model.setValorPrevisto(request.getValorPrevisto());
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
        model.setPago(model.getPagamentos() != null && !model.getPagamentos().isEmpty());

        return model;
    }

    public static void updateEntity(Auto model, AutoUpdateRequest request) {
        if (request == null || model == null) return;

        if (request.getPlaca() != null) model.setPlaca(request.getPlaca());
        if (request.getTipo() != null) model.setTipo(request.getTipo());
        if (request.getValor() != null) model.setValor(request.getValor());
        if (request.getPago() != null) model.setPago(request.getPago());
        // null = não mexe; "" = remove a foto
        if (request.getFoto() != null) model.setFoto(request.getFoto().isBlank() ? null : request.getFoto());
        if (request.getSaida() != null) model.setSaida(request.getSaida() == 0 ? null : request.getSaida());
        if (request.getValorPrevisto() != null) model.setValorPrevisto(request.getValorPrevisto());

        if (request.getPagamentos() != null) {
            // Só reaproveita id/data de pagamentos que já pertencem a ESTE veículo;
            // um id desconhecido vindo do cliente é ignorado (ganha UUID novo).
            Map<String, Pagamento> existentes = new HashMap<>();
            for (Pagamento antigo : model.getPagamentos()) existentes.put(antigo.getId(), antigo);

            List<Pagamento> novos = new ArrayList<>();
            for (PagamentoRequest pr : request.getPagamentos()) {
                Pagamento p = toPagamentoEntity(pr);
                Pagamento antigo = pr.getId() == null ? null : existentes.get(pr.getId());
                if (antigo != null) {
                    p.setId(antigo.getId());
                    p.setData(antigo.getData());
                }
                p.setAutoId(model.getId());
                novos.add(p);
            }
            model.getPagamentos().clear();
            model.getPagamentos().addAll(novos);
        }
    }

    public static AutoResponse toResponse(Auto model) {
        if (model == null) return null;

        List<PagamentoResponse> pagamentosResponse = new ArrayList<>();
        if (model.getPagamentos() != null) {
            for (Pagamento p : model.getPagamentos()) {
                pagamentosResponse.add(toPagamentoResponse(p));
            }
        }

        AutoResponse response = new AutoResponse(
            model.getId(),
            model.getPlaca(),
            model.getTipo(),
            model.getValor(),
            model.getPago(),
            model.getEntrada(),
            pagamentosResponse
        );
        response.setValorPrevisto(model.getValorPrevisto());
        response.setFoto(model.getFoto());
        response.setSaida(model.getSaida());
        return response;
    }

    public static Pagamento toPagamentoEntity(PagamentoRequest request) {
        if (request == null) return null;

        Pagamento p = new Pagamento();
        p.setId(UUID.randomUUID().toString());
        p.setMetodoPagamento(request.getMetodo());
        p.setValor(request.getValor());
        p.setTrocoDinheiro(request.getTrocoDinheiro());
        p.setTrocoPix(request.getTrocoPix());
        return p;
    }

    public static PagamentoResponse toPagamentoResponse(Pagamento entity) {
        if (entity == null) return null;

        return new PagamentoResponse(
            entity.getId(),
            entity.getMetodoPagamento(),
            entity.getValor(),
            entity.getTrocoDinheiro(),
            entity.getTrocoPix(),
            entity.getData()
        );
    }
}