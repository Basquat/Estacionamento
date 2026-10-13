package basquat.estacionamento.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PagamentoRequest {

    @NotBlank(message = "O método de pagamento é obrigatório")
    private String metodo;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    private Double valor;

    private Double troco;

    public PagamentoRequest() {}

    public PagamentoRequest(String metodo, Double valor, Double troco) {
        this.metodo = metodo;
        this.valor = valor;
        this.troco = troco;
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
}