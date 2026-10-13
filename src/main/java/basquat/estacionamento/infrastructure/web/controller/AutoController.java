package basquat.estacionamento.infrastructure.web.controller;

import basquat.estacionamento.application.dto.request.AutoCreateRequest;
import basquat.estacionamento.application.dto.request.AutoUpdateRequest;
import basquat.estacionamento.application.dto.response.AutoListResponse;
import basquat.estacionamento.application.dto.response.AutoResponse;
import basquat.estacionamento.application.port.in.AutoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para veículos.
 * Fica em infrastructure.web.controller.
 */
@RestController
@RequestMapping({"/Automoveis", "/api/Automoveis"})
@CrossOrigin(origins = "*")
public class AutoController {

    private final AutoUseCase autoService;

    public AutoController(AutoUseCase autoService) {
        this.autoService = autoService;
    }

    @GetMapping
    public AutoListResponse getAllModels() {
        return autoService.listar();
    }

    @GetMapping("/{id}")
    public AutoResponse getModelById(@PathVariable String id) {
        return autoService.buscar(id);
    }

    @PostMapping("/Add")
    public AutoResponse addCarro(@Valid @RequestBody AutoCreateRequest request) {
        return autoService.criar(request);
    }

    @PutMapping("/{id}")
    public AutoResponse putModel(@PathVariable String id, @Valid @RequestBody AutoUpdateRequest request) {
        return autoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuto(@PathVariable String id) {
        autoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}