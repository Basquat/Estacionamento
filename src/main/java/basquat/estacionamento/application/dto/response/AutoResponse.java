package basquat.estacionamento.application.dto.response;

import java.util.List;

public class AutoResponse {

    private String id;
    private String placa;
    private String tipo;
    private Double valor;
    private Boolean pago;
    private Long entrada;
    private List<PagamentoResponse> pagamentos;

    public AutoResponse() {}

    public AutoResponse(String id, String placa, String tipo, Double valor, Boolean pago, Long entrada, List<PagamentoResponse> pagamentos) {
        this.id = id;
        this.placa = placa;
        this.tipo = tipo;
        this.valor = valor;
        this.pago = pago;
        this.entrada = entrada;
        this.pagamentos = pagamentos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getPago() {
        return pago;
    }

    public void setPago(Boolean pago) {
        this.pago = pago;
    }

    public Long getEntrada() {
        return entrada;
    }

    public void setEntrada(Long entrada) {
        this.entrada = entrada;
    }

    public List<PagamentoResponse> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<PagamentoResponse> pagamentos) {
        this.pagamentos = pagamentos;
    }
}