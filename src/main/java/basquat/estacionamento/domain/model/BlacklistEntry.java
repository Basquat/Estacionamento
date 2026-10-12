package basquat.estacionamento.domain.model;

import java.util.UUID;

/**
 * Entidade de domínio - Registro de blacklist.
 * Sem dependências de JPA ou Spring.
 */
public class BlacklistEntry {

    private String id;
    private String placa;
    private String motivo;
    private String foto;
    private Long criadoEm;

    public BlacklistEntry() {}

    public BlacklistEntry(String id, String placa, String motivo, String foto, Long criadoEm) {
        this.id = id;
        this.placa = placa;
        this.motivo = motivo;
        this.foto = foto;
        this.criadoEm = criadoEm;
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