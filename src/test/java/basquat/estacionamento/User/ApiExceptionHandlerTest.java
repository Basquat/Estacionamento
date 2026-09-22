package basquat.estacionamento.User;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void recursoNaoEncontradoVira404() {
        ResponseEntity<Map<String, Object>> resposta =
                handler.naoEncontrado(new RecursoNaoEncontradoException("sumiu"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).containsEntry("message", "sumiu");
    }

    @Test
    void regraNegocioVira409() {
        ResponseEntity<Map<String, Object>> resposta =
                handler.regraNegocio(new RegraNegocioException("placa duplicada"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody()).containsEntry("message", "placa duplicada");
    }

    @Test
    void runtimeExceptionGenericaVira400ComMensagemPadrao() {
        ResponseEntity<Map<String, Object>> resposta =
                handler.generico(new RuntimeException());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).containsEntry("message", "Erro ao processar a requisição");
    }
}
