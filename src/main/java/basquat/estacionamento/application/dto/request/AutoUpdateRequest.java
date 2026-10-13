package basquat.estacionamento.application.dto.request;

import java.util.List;

public class AutoUpdateRequest {

    private String placa;
    private String tipo;
    private Double valor;
    private Double valorPrevisto;
    private Boolean pago;
    private List<PagamentoRequest> pagamentos;
    private String foto; // data URL base64; null = sem foto

    public AutoUpdateRequest() {}

    public AutoUpdateRequest(String placa, String tipo, Double valor, Boolean pago, List<PagamentoRequest> pagamentos) {
        this.placa = placa;
        this.tipo = tipo;
        this.valor = valor;
        this.pago = pago;
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

    public Boolean getPago() {
        return pago;
    }

    public void setPago(Boolean pago) {
        this.pago = pago;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
