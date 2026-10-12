package basquat.estacionamento.application.dto.response;

import java.util.List;

public class AutoListResponse {

    private java.util.List<AutoResponse> automoveis;
    private int total;

    public AutoListResponse() {}

    public AutoListResponse(List<AutoResponse> automoveis, int total) {
        this.automoveis = automoveis;
        this.total = total;
    }

    public List<AutoResponse> getAutomoveis() {
        return automoveis;
    }

    public void setAutomoveis(List<AutoResponse> automoveis) {
        this.automoveis = automoveis;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}