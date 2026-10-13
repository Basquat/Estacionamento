package basquat.estacionamento.infrastructure.event;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Serviço de eventos SSE.
 * Fica em infrastructure.event.
 */
@Service
public class EventoService {

    private final java.util.List<SseEmitter> emitters = new java.util.ArrayList<>();

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(60000L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        return emitter;
    }

    public void notificar() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("atualizacao").data("atualizado"));
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }
}