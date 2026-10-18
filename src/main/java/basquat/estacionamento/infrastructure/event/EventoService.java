package basquat.estacionamento.infrastructure.event;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Serviço de eventos SSE.
 * Fica em infrastructure.event.
 * - Timeout 5 min (300s) para resistir ao proxy do Render free (~60s ocioso).
 * - Heartbeat a cada 25s (comment) mantem a conexao viva.
 * - Evento inicial imediato ("conectado") para o front saber que estabeleceu.
 * - Cleanup em onCompletion, onTimeout, onError.
 * - CopyOnWriteArrayList para concorrencia segura sem locks.
 */
@Service
public class EventoService implements basquat.estacionamento.application.port.out.NotificadorPort {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(300000L); // 5 min
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        // Evento inicial imediato: o front sabe que a conexao estabeleceu
        try {
            emitter.send(SseEmitter.event().name("conectado").data("ok"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        return emitter;
    }

    @Override
    public void notificar() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("atualizacao").data("atualizado"));
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }

    // Heartbeat a cada 25s: comment line nao dispara evento no front, so mantem a conexao viva no proxy
    @Scheduled(fixedRate = 25000)
    public void heartbeat() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }
}