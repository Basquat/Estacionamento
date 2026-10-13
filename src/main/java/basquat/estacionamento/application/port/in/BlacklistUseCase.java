package basquat.estacionamento.application.port.in;

import basquat.estacionamento.application.dto.request.BlacklistCreateRequest;
import basquat.estacionamento.application.dto.response.BlacklistResponse;

import java.util.List;

/** Porta de entrada - casos de uso da blacklist. */
public interface BlacklistUseCase {
    List<BlacklistResponse> listar();
    BlacklistResponse adicionar(BlacklistCreateRequest request);
    void remover(String id);
}
