package basquat.estacionamento.infrastructure.web.controller;

import basquat.estacionamento.application.dto.request.BlacklistCreateRequest;
import basquat.estacionamento.application.dto.response.BlacklistResponse;
import basquat.estacionamento.application.port.in.BlacklistUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para blacklist.
 * Fica em infrastructure.web.controller.
 */
@RestController
@RequestMapping("/Blacklist")
@CrossOrigin(origins = "*")
public class BlacklistController {

    private final BlacklistUseCase blacklistService;

    public BlacklistController(BlacklistUseCase blacklistService) {
        this.blacklistService = blacklistService;
    }

    @GetMapping
    public List<BlacklistResponse> listar() {
        return blacklistService.listar();
    }

    @PostMapping("/Add")
    public BlacklistResponse adicionar(@Valid @RequestBody BlacklistCreateRequest request) {
        return blacklistService.adicionar(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        blacklistService.remover(id);
        return ResponseEntity.noContent().build();
    }
}