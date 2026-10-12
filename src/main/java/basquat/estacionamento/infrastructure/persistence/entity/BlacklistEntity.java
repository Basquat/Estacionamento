package basquat.estacionamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entidade JPA para blacklist.
 * Fica em infrastructure - é a representação persistida.
 */
@Entity
@Table(name = "blacklist")
public class BlacklistEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "placa", nullable = false)
    private String placa;

    @Column(name = "motivo", length = 500)
    private String motivo;

    @Column(name = "foto", columnDefinition = "TEXT")
    private String foto;

    @Column(name = "criado_em", nullable = false)
    private Long criadoEm;

    @PrePersist
    void preencherPadroes() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (criadoEm == null) {
            criadoEm = System.currentTimeMillis();
        }
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }
}