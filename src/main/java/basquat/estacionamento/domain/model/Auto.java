package basquat.estacionamento.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade de domínio - Veículo estacionado.
 * Sem dependências de JPA ou Spring.
 */
public class Auto {

    private String id;
    private String placa;
    private String tipo;
    private double valor;
    private Double valorPrevisto;
    private Boolean pago;
    private Long entrada;
    private List<Pagamento> pagamentos = new ArrayList<>();

    public Auto() {}

    public Auto(String id, String placa, String tipo, double valor, Boolean pago, Long entrada, List<Pagamento> pagamentos) {
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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
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

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos.clear();
        if (pagamentos != null) {
            this.pagamentos.addAll(pagamentos);
        }
    }

    public Double getValorPrevisto() {
        return valorPrevisto;
    }

    public void setValorPrevisto(Double valorPrevisto) {
        this.valorPrevisto = valorPrevisto;
    }
}
