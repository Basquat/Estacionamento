package basquat.estacionamento.application.port.in;

import basquat.estacionamento.application.dto.request.AutoCreateRequest;
import basquat.estacionamento.application.dto.request.AutoUpdateRequest;
import basquat.estacionamento.application.dto.response.AutoListResponse;
import basquat.estacionamento.application.dto.response.AutoResponse;

/** Porta de entrada - casos de uso de veículos. */
public interface AutoUseCase {
    AutoListResponse listar();
    AutoResponse buscar(String id);
    AutoResponse criar(AutoCreateRequest request);
    AutoResponse atualizar(String id, AutoUpdateRequest request);
    void remover(String id);
}
