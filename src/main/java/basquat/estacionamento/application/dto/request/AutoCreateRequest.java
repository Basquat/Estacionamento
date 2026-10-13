package basquat.estacionamento.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class AutoCreateRequest {

    @NotBlank(message = "A placa é obrigatória")
    private String placa;

    @NotBlank(message = "O tipo é obrigatório")
    private String tipo;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    private Double valor;
    private Double valorPrevisto;

    private List<PagamentoRequest> pagamentos;

    public AutoCreateRequest() {}

    public AutoCreateRequest(String placa, String tipo, Double valor, List<PagamentoRequest> pagamentos) {
        this.placa = placa;
        this.tipo = tipo;
        this.valor = valor;
        this.pagamentos = pagamentos;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public List<PagamentoRequest> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<PagamentoRequest> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public Double getValorPrevisto() {
        return valorPrevisto;
    }

    public void setValorPrevisto(Double valorPrevisto) {
        this.valorPrevisto = valorPrevisto;
    }
}
