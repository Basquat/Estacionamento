package basquat.estacionamento.application.port.out;

import basquat.estacionamento.domain.model.Pagamento;

/**
 * Porta de saída - Repositório de Pagamento.
 * Quem implementa fica em infrastructure.
 */
public interface PagamentoRepositoryPort {
    void deleteByAutoId(String autoId);
}