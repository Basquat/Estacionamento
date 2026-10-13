package basquat.estacionamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entidade JPA para pagamentos.
 * Fica em infrastructure - é a representação persistida.
 */
@Entity
@Table(name = "pagamento")
public class PagamentoEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "auto_id", nullable = false)
    private String autoId;

    @Column(name = "metodo_pagamento", nullable = false)
    private String metodoPagamento;

    @Column(name = "valor", nullable = false)
    private double valor;

    @Column(name = "troco")
    private Double troco;

    @Column(name = "data", nullable = false)
    private Long data;

    @PrePersist
    @PreUpdate
    void preencherPadroes() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (data == null) {
            data = System.currentTimeMillis();
        }
        if (troco == null || troco < 0) {
            troco = 0.0;
        }
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