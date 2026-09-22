package basquat.estacionamento.User;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Blacklist")
@CrossOrigin(origins = "*")
public class BlacklistController {

    private final BlacklistRepository repository;
    private final EventoService eventoService;

    public BlacklistController(BlacklistRepository repository, EventoService eventoService) {
        this.repository = repository;
        this.eventoService = eventoService;
    }

    @GetMapping
    public List<BlacklistEntry> listar() {
        return repository.findAllByOrderByCriadoEmDesc();
    }

    @PostMapping("/Add")
    public BlacklistEntry adicionar(@RequestBody BlacklistEntry entry) {
        if (entry.getPlaca() == null || entry.getPlaca().isBlank()) {
            throw new RegraNegocioException("A placa é obrigatória.");
        }
        entry.setId(null);
        BlacklistEntry salvo = repository.save(entry);
        eventoService.notificar();
        return salvo;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Registro da blacklist não encontrado");
        }
        repository.deleteById(id);
        eventoService.notificar();
        return ResponseEntity.noContent().build();
    }
}
