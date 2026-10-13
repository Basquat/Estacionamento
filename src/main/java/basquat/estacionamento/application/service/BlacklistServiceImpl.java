package basquat.estacionamento.application.service;

import basquat.estacionamento.application.dto.request.BlacklistCreateRequest;
import basquat.estacionamento.application.dto.response.BlacklistResponse;
import basquat.estacionamento.application.mapper.BlacklistMapper;
import basquat.estacionamento.application.port.out.BlacklistRepositoryPort;
import basquat.estacionamento.domain.exception.RegraNegocioException;
import basquat.estacionamento.domain.model.BlacklistEntry;
import basquat.estacionamento.infrastructure.event.EventoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlacklistServiceImpl {

    private final BlacklistRepositoryPort repository;
    private final EventoService eventoService;

    public BlacklistServiceImpl(BlacklistRepositoryPort repository, EventoService eventoService) {
        this.repository = repository;
        this.eventoService = eventoService;
    }

    public List<BlacklistResponse> listar() {
        return repository.findAllByCriadoEmDesc().stream()
                .map(BlacklistMapper::toResponse)
                .toList();
    }

    @Transactional
    public BlacklistResponse adicionar(BlacklistCreateRequest request) {
        if (request.getPlaca() == null || request.getPlaca().isBlank()) {
            throw new RegraNegocioException("A placa é obrigatória.");
        }
        BlacklistEntry entry = BlacklistMapper.toEntity(request);
        BlacklistEntry salvo = repository.save(entry);
        eventoService.notificar();
        return BlacklistMapper.toResponse(salvo);
    }

    @Transactional
    public void remover(String id) {
        if (!repository.existsById(id)) {
            throw new basquat.estacionamento.domain.exception.RecursoNaoEncontradoException("Registro da blacklist não encontrado");
        }
        repository.deleteById(id);
        eventoService.notificar();
    }
}