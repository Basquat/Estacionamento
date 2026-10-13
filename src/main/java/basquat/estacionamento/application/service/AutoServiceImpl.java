package basquat.estacionamento.application.service;

import basquat.estacionamento.application.dto.request.AutoCreateRequest;
import basquat.estacionamento.application.dto.request.AutoUpdateRequest;
import basquat.estacionamento.application.dto.response.AutoListResponse;
import basquat.estacionamento.application.dto.response.AutoResponse;
import basquat.estacionamento.application.mapper.Mapper;
import basquat.estacionamento.application.port.out.AutoRepositoryPort;
import basquat.estacionamento.application.port.out.PagamentoRepositoryPort;
import basquat.estacionamento.domain.exception.RecursoNaoEncontradoException;
import basquat.estacionamento.domain.exception.RegraNegocioException;
import basquat.estacionamento.domain.model.Auto;
import basquat.estacionamento.infrastructure.event.EventoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutoServiceImpl {

    private final AutoRepositoryPort autoRepository;
    private final PagamentoRepositoryPort pagamentoRepository;
    private final EventoService eventoService;

    public AutoServiceImpl(AutoRepositoryPort autoRepository,
                           PagamentoRepositoryPort pagamentoRepository,
                           EventoService eventoService) {
        this.autoRepository = autoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.eventoService = eventoService;
    }

    public AutoListResponse listar() {
        List<AutoResponse> autos = autoRepository.findAll().stream()
                .map(Mapper::toResponse)
                .toList();
        return new AutoListResponse(autos, autos.size());
    }

    public AutoResponse buscar(String id) {
        Auto model = autoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado"));
        return Mapper.toResponse(model);
    }

    @Transactional
    public AutoResponse criar(AutoCreateRequest request) {
        Auto model = Mapper.toEntity(request);
        if (model.getPlaca() == null || model.getPlaca().isBlank()) {
            throw new RegraNegocioException("A placa é obrigatória.");
        }
        if (autoRepository.existsByPlacaIgnoreCase(model.getPlaca())) {
            throw new RegraNegocioException("A placa " + model.getPlaca() + " já está cadastrada.");
        }
        Auto salvo = autoRepository.save(model);
        eventoService.notificar();
        return Mapper.toResponse(salvo);
    }

    @Transactional
    public AutoResponse atualizar(String id, AutoUpdateRequest request) {
        Auto model = autoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado"));

        if (request.getPlaca() != null && !model.getPlaca().equalsIgnoreCase(request.getPlaca())
                && autoRepository.existsByPlacaIgnoreCase(request.getPlaca())) {
            throw new RegraNegocioException("A placa " + request.getPlaca() + " já está cadastrada.");
        }

        Mapper.updateEntity(model, request);
        Auto salvo = autoRepository.save(model);
        eventoService.notificar();
        return Mapper.toResponse(salvo);
    }

    @Transactional
    public void remover(String id) {
        if (!autoRepository.findById(id).isPresent()) {
            throw new RecursoNaoEncontradoException("Automóvel não encontrado");
        }
        pagamentoRepository.deleteByAutoId(id);
        int removidos = autoRepository.deleteByIdReturningCount(id);
        if (removidos == 0) {
            throw new RecursoNaoEncontradoException("Automóvel não encontrado");
        }
        eventoService.notificar();
    }
}