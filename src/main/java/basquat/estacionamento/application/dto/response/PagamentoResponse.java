package basquat.estacionamento.application.dto.response;

public class PagamentoResponse {

    private String id;
    private String metodo;
    private Double valor;
    private Double troco;
    private Long data;

    public PagamentoResponse() {}

    public PagamentoResponse(String id, String metodo, Double valor, Double troco, Long data) {
        this.id = id;
        this.metodo = metodo;
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

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Double getTroco() {
        return troco;
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