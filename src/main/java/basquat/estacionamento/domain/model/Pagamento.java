package basquat.estacionamento.domain.model;

import java.util.UUID;

/**
 * Entidade de domínio - Forma de pagamento de um veículo.
 * Sem dependências de JPA ou Spring.
 */
public class Pagamento {

    private String id;
    private String autoId;
    private String metodoPagamento;
    private double valor;
    private Double troco;
    private Long data;

    public Pagamento() {}

    public Pagamento(String id, String autoId, String metodoPagamento, double valor, Double troco, Long data) {
        this.id = id;
        this.autoId = autoId;
        this.metodoPagamento = metodoPagamento;
        this.valor = valor;
        this.troco = troco;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAutoId() {
        return autoId;
    }

    public void setAutoId(String autoId) {
        this.autoId = autoId;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Double getTroco() {
        return troco == null ? 0.0 : troco;
    }

    public void setTroco(Double troco) {
        this.troco = troco;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }
}