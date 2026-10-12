package basquat.estacionamento.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public class BlacklistCreateRequest {

    @NotBlank(message = "A placa é obrigatória")
    private String placa;

    private String motivo;
    private String foto; // base64

    public BlacklistCreateRequest() {}

    public BlacklistCreateRequest(String placa, String motivo, String foto) {
        this.placa = placa;
        this.motivo = motivo;
        this.foto = foto;
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
}