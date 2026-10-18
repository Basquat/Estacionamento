package basquat.estacionamento.infrastructure.web.controller;

import basquat.estacionamento.application.port.out.AutoRepositoryPort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para health check.
 * Fica em infrastructure.web.controller.
 * Valida que o Spring esta subido e que consegue falar com o banco.
 */
@RestController
@CrossOrigin(origins = "*")
public class HealthController {

    private final AutoRepositoryPort autoRepository;

    public HealthController(AutoRepositoryPort autoRepository) {
        this.autoRepository = autoRepository;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        try {
            autoRepository.findAll();
            status.put("status", "UP");
            status.put("timestamp", System.currentTimeMillis());
            status.put("database", "UP");
        } catch (Exception e) {
            status.put("status", "DOWN");
            status.put("timestamp", System.currentTimeMillis());
            status.put("database", "DOWN");
            status.put("error", e.getMessage());
        }
        return status;
    }
}
