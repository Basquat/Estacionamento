package basquat.estacionamento.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PagamentoRequest {

    /** Opcional: id de um pagamento já existente do mesmo veículo (preserva id e data). */
    private String id;

    @NotBlank(message = "O método de pagamento é obrigatório")
    private String metodo;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    private Double valor;

    private Double trocoDinheiro;
    private Double trocoPix;

    public PagamentoRequest() {}

    public PagamentoRequest(String metodo, Double valor, Double trocoDinheiro, Double trocoPix) {
        this.metodo = metodo;
        this.valor = valor;
        this.trocoDinheiro = trocoDinheiro;
        this.trocoPix = trocoPix;
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
}