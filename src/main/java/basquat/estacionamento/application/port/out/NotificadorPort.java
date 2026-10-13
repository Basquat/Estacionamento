package basquat.estacionamento.application.port.out;

/** Porta de saída - avisa os clientes conectados que os dados mudaram. */
public interface NotificadorPort {
    void notificar();
}
