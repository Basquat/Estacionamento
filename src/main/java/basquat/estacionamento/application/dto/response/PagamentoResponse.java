package basquat.estacionamento.application.dto.response;

public class PagamentoResponse {

    private String id;
    private String metodo;
    private Double valor;
    private Double trocoDinheiro;
    private Double trocoPix;
    private Long data;

    public PagamentoResponse() {}

    public PagamentoResponse(String id, String metodo, Double valor, Double trocoDinheiro, Double trocoPix, Long data) {
        this.id = id;
        this.metodo = metodo;
        this.valor = valor;
        this.trocoDinheiro = trocoDinheiro;
        this.trocoPix = trocoPix;
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

    public Double getTrocoDinheiro() {
        return trocoDinheiro;
    }

    public void setTrocoDinheiro(Double trocoDinheiro) {
        this.trocoDinheiro = trocoDinheiro;
    }

    public Double getTrocoPix() {
        return trocoPix;
    }

    public void setTrocoPix(Double trocoPix) {
        this.trocoPix = trocoPix;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }
}