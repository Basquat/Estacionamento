package basquat.estacionamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA para veículos.
 * Fica em infrastructure - é a representação persistida.
 */
@Entity
@Table(name = "auto_model")
public class AutoEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "placa", nullable = false, unique = true)
    private String placa;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "valor", nullable = false)
    private double valor;

    @Column(name = "valor_previsto")
    private Double valorPrevisto;

    @Column(name = "pago", nullable = false)
    private Boolean pago;

    @Column(name = "entrada", nullable = false)
    private Long entrada;

    @Column(name = "saida")
    private Long saida;

    @Column(name = "foto", columnDefinition = "TEXT")
    private String foto;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @BatchSize(size = 200)
    @JoinColumn(
            name = "auto_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private List<PagamentoEntity> pagamentos = new ArrayList<>();

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

    public List<PagamentoEntity> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<PagamentoEntity> pagamentos) {
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Long getSaida() {
        return saida;
    }

    public void setSaida(Long saida) {
        this.saida = saida;
    }
}
