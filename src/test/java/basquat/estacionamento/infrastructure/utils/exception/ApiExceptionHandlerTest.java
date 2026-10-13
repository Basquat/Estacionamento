package basquat.estacionamento.infrastructure.utils.exception;

import basquat.estacionamento.domain.exception.RecursoNaoEncontradoException;
import basquat.estacionamento.domain.exception.RegraNegocioException;
import basquat.estacionamento.infrastructure.utils.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void recursoNaoEncontradoVira404() {
        ResponseEntity<Map<String, Object>> resposta =
                handler.handleNaoEncontrado(new RecursoNaoEncontradoException("sumiu"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).containsEntry("status", 404);
        assertThat(resposta.getBody()).containsEntry("message", "sumiu");
    }

    @Test
    void regraNegocioVira409() {
        ResponseEntity<Map<String, Object>> resposta =
                handler.handleRegraNegocio(new RegraNegocioException("placa duplicada"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody()).containsEntry("status", 409);
        assertThat(resposta.getBody()).containsEntry("message", "placa duplicada");
    }
}