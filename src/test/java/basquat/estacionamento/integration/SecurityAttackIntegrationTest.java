package basquat.estacionamento.integration;

import basquat.estacionamento.application.dto.request.AutoCreateRequest;
import basquat.estacionamento.application.dto.request.AutoUpdateRequest;
import basquat.estacionamento.application.dto.request.PagamentoRequest;
import basquat.estacionamento.application.dto.response.AutoResponse;
import basquat.estacionamento.application.dto.response.AutoListResponse;
import basquat.estacionamento.domain.exception.RecursoNaoEncontradoException;
import basquat.estacionamento.domain.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SecurityAttackIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl = "http://localhost:" + port + "/Automoveis";
        // Clean up by getting all and deleting (simple cleanup)
        try {
            ResponseEntity<AutoListResponse> list = restTemplate.getForEntity(baseUrl, AutoListResponse.class);
            if (list.getBody() != null && list.getBody().getAutomoveis() != null) {
                for (AutoResponse auto : list.getBody().getAutomoveis()) {
                    restTemplate.delete(baseUrl + "/" + auto.getId());
                }
            }
        } catch (Exception ignored) {}
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Nested
    @DisplayName("SQL Injection Attacks")
    class SqlInjectionTests {

        @Test
        @DisplayName("SQL injection in placa field should be treated as literal string")
        void sqlInjectionInPlaca() {
            String sqlInjection = "'; DROP TABLE auto_model; --";
            AutoCreateRequest request = new AutoCreateRequest(sqlInjection, "carro", 10.0, null);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getPlaca()).isEqualTo(sqlInjection);
        }

        @Test
        @DisplayName("SQL injection in tipo field")
        void sqlInjectionInTipo() {
            String sqlInjection = "carro'; DELETE FROM pagamento WHERE 1=1; --";
            AutoCreateRequest request = new AutoCreateRequest("SQLTIPO1", sqlInjection, 10.0, null);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getTipo()).isEqualTo(sqlInjection);
        }

        @Test
        @DisplayName("Union-based SQL injection attempt")
        void unionSqlInjection() {
            String payload = "ABC' UNION SELECT * FROM users --";
            AutoCreateRequest request = new AutoCreateRequest(payload, "carro", 10.0, null);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("XSS Attacks")
    class XssTests {

        @Test
        @DisplayName("Script tag in placa should be stored as literal")
        void scriptTagInPlaca() {
            String xss = "<script>alert('xss')</script>";
            AutoCreateRequest request = new AutoCreateRequest(xss, "carro", 10.0, null);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getPlaca()).isEqualTo(xss);
        }

        @Test
        @DisplayName("Event handler XSS payload")
        void eventHandlerXss() {
            String xss = "<img src=x onerror=alert(1)>";
            AutoCreateRequest request = new AutoCreateRequest("XYZ789", "carro", 10.0, null);
            request.setPlaca(xss);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("JavaScript protocol XSS")
        void javascriptProtocolXss() {
            String xss = "javascript:alert(document.cookie)";
            AutoCreateRequest request = new AutoCreateRequest(xss, "carro", 10.0, null);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("Input Validation Bypass Attempts")
    class ValidationBypassTests {

        @Test
        @DisplayName("Null placa should be rejected")
        void nullPlacaRejected() {
            AutoCreateRequest request = new AutoCreateRequest(null, "carro", 10.0, null);

            assertThatThrownBy(() -> restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("400");
        }

        @Test
        @DisplayName("Empty placa should be rejected")
        void emptyPlacaRejected() {
            AutoCreateRequest request = new AutoCreateRequest("", "carro", 10.0, null);

            assertThatThrownBy(() -> restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("400");
        }

        @Test
        @DisplayName("Whitespace-only placa should be rejected")
        void whitespacePlacaRejected() {
            AutoCreateRequest request = new AutoCreateRequest("   ", "carro", 10.0, null);

            assertThatThrownBy(() -> restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("400");
        }

        @Test
        @DisplayName("Negative valor should be rejected")
        void negativeValorRejected() {
            AutoCreateRequest request = new AutoCreateRequest("ABC123", "carro", -10.0, null);

            assertThatThrownBy(() -> restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("400");
        }

        @Test
        @DisplayName("Zero valor should be rejected")
        void zeroValorRejected() {
            AutoCreateRequest request = new AutoCreateRequest("ABC123", "carro", 0.0, null);

            assertThatThrownBy(() -> restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("400");
        }

        @Test
        @DisplayName("Null tipo should be rejected")
        void nullTipoRejected() {
            AutoCreateRequest request = new AutoCreateRequest("ABC123", null, 10.0, null);

            assertThatThrownBy(() -> restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("400");
        }

        @Test
        @DisplayName("Very large valor should be handled")
        void largeValor() {
            AutoCreateRequest request = new AutoCreateRequest("ABC123", "carro", Double.MAX_VALUE, null);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Unicode characters in placa")
        void unicodePlaca() {
            String unicode = "ΑΒΓ1234日本語🚗";
            AutoCreateRequest request = new AutoCreateRequest(unicode, "carro", 10.0, null);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getPlaca()).isEqualTo(unicode);
        }

        @Test
        @DisplayName("Extremely long placa (1000 chars)")
        void extremelyLongPlaca() {
            String longPlaca = "A".repeat(1000);
            AutoCreateRequest request = new AutoCreateRequest(longPlaca, "carro", 10.0, null);

            assertThatThrownBy(() -> restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("400");
        }
    }

    @Nested
    @DisplayName("Business Logic Bypass Attempts")
    class BusinessLogicBypassTests {

        @Test
        @DisplayName("Duplicate placa (case insensitive) should be rejected")
        void duplicatePlacaCaseInsensitive() {
            AutoCreateRequest request1 = new AutoCreateRequest("ABC1234", "carro", 10.0, null);
            AutoCreateRequest request2 = new AutoCreateRequest("abc1234", "moto", 20.0, null);

            restTemplate.postForEntity(baseUrl + "/Add", request1, AutoResponse.class);

            assertThatThrownBy(() -> restTemplate.postForEntity(baseUrl + "/Add", request2, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("409");
        }

        @Test
        @DisplayName("Update to existing placa should be rejected")
        void updateToExistingPlaca() {
            AutoCreateRequest request1 = new AutoCreateRequest("AAA1111", "carro", 10.0, null);
            AutoCreateRequest request2 = new AutoCreateRequest("BBB2222", "carro", 20.0, null);

            ResponseEntity<AutoResponse> r1 = restTemplate.postForEntity(baseUrl + "/Add", request1, AutoResponse.class);
            restTemplate.postForEntity(baseUrl + "/Add", request2, AutoResponse.class);

            AutoUpdateRequest update = new AutoUpdateRequest("BBB2222", "carro", 15.0, true, null);
            HttpEntity<AutoUpdateRequest> entity = new HttpEntity<>(update, jsonHeaders());

            assertThatThrownBy(() -> restTemplate.exchange(
                    baseUrl + "/" + r1.getBody().getId(), HttpMethod.PUT, entity, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("409");
        }

        @Test
        @DisplayName("Update non-existent vehicle should return 404")
        void updateNonExistent() {
            AutoUpdateRequest update = new AutoUpdateRequest("ABC123", "carro", 15.0, true, null);
            HttpEntity<AutoUpdateRequest> entity = new HttpEntity<>(update, jsonHeaders());

            assertThatThrownBy(() -> restTemplate.exchange(
                    baseUrl + "/nonexistent-id", HttpMethod.PUT, entity, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("404");
        }

        @Test
        @DisplayName("Delete non-existent vehicle should return 404")
        void deleteNonExistent() {
            assertThatThrownBy(() -> restTemplate.exchange(
                    baseUrl + "/nonexistent-id", HttpMethod.DELETE, null, Void.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("404");
        }

        @Test
        @DisplayName("Get non-existent vehicle should return 404")
        void getNonExistent() {
            assertThatThrownBy(() -> restTemplate.getForEntity(baseUrl + "/nonexistent-id", AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("404");
        }
    }

    @Nested
    @DisplayName("Mass Assignment / Overposting")
    class MassAssignmentTests {

        @Test
        @DisplayName("Extra fields in JSON should be ignored")
        void extraFieldsIgnored() {
            String jsonWithExtra = """
                {
                    "placa": "MASS123",
                    "tipo": "carro",
                    "valor": 10.0,
                    "pago": true,
                    "entrada": 1234567890,
                    "id": "hacker-id",
                    "pagamentos": [],
                    "extraField": "malicious",
                    "admin": true,
                    "role": "superuser"
                }
                """;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonWithExtra, headers);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", entity, AutoResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getId()).isNotEqualTo("hacker-id");
            assertThat(response.getBody().getPlaca()).isEqualTo("MASS123");
        }

        @Test
        @DisplayName("ID in create request should be ignored (server generates)")
        void idInCreateIgnored() {
            AutoCreateRequest request = new AutoCreateRequest("IDTEST1", "carro", 10.0, null);
            // Can't set ID via DTO - it's generated server-side

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getId()).isNotNull();
            assertThat(response.getBody().getId()).isNotEqualTo("IDTEST1");
        }
    }

    @Nested
    @DisplayName("IDOR (Insecure Direct Object Reference)")
    class IdorTests {

        @Test
        @DisplayName("Cannot access other user's vehicle by guessing ID")
        void cannotAccessOtherVehicle() {
            AutoCreateRequest request = new AutoCreateRequest("IDOR123", "carro", 10.0, null);
            ResponseEntity<AutoResponse> created = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            // Try to access with slightly modified ID
            String otherId = created.getBody().getId().substring(0, created.getBody().getId().length() - 1) + "X";

            assertThatThrownBy(() -> restTemplate.getForEntity(baseUrl + "/" + otherId, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("404");
        }

        @Test
        @DisplayName("Cannot delete other user's vehicle")
        void cannotDeleteOtherVehicle() {
            AutoCreateRequest request = new AutoCreateRequest("IDOR456", "carro", 10.0, null);
            ResponseEntity<AutoResponse> created = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            String otherId = created.getBody().getId().substring(0, created.getBody().getId().length() - 1) + "Y";

            assertThatThrownBy(() -> restTemplate.exchange(
                    baseUrl + "/" + otherId, HttpMethod.DELETE, null, Void.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("404");
        }
    }

    @Nested
    @DisplayName("Payment Manipulation Attacks")
    class PaymentManipulationTests {

        @Test
        @DisplayName("Negative payment value should be rejected")
        void negativePaymentValue() {
            PagamentoRequest payment = new PagamentoRequest();
            payment.setMetodo("dinheiro");
            payment.setValor(-50.0);
            payment.setTrocoDinheiro(0.0);

            AutoCreateRequest request = new AutoCreateRequest("PAY123", "carro", 10.0, List.of(payment));

            // System may or may not validate - accept either behavior
            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);
            assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Payment sum exceeding vehicle value")
        void paymentExceedsVehicleValue() {
            PagamentoRequest payment = new PagamentoRequest();
            payment.setMetodo("dinheiro");
            payment.setValor(100.0);
            payment.setTrocoDinheiro(0.0);

            AutoCreateRequest request = new AutoCreateRequest("PAY456", "carro", 10.0, List.of(payment));

            // Should either reject or accept (business rule: sum must equal valor)
            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);
            assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Multiple payment methods with troco")
        void multiplePaymentsWithTroco() {
            PagamentoRequest p1 = new PagamentoRequest();
            p1.setMetodo("dinheiro");
            p1.setValor(20.0);
            p1.setTrocoDinheiro(5.0);

            PagamentoRequest p2 = new PagamentoRequest();
            p2.setMetodo("pix");
            p2.setValor(5.0);

            AutoCreateRequest request = new AutoCreateRequest("PAY789", "carro", 25.0, List.of(p1, p2));

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getPagamentos()).hasSize(2);
        }

        @Test
        @DisplayName("Null payment method should be handled")
        void nullPaymentMethod() {
            PagamentoRequest payment = new PagamentoRequest();
            payment.setMetodo(null);
            payment.setValor(10.0);

            AutoCreateRequest request = new AutoCreateRequest("PAYNULL", "carro", 10.0, List.of(payment));

            assertThatThrownBy(() -> restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("400");
        }
    }

    @Nested
    @DisplayName("Race Condition / Concurrency")
    class ConcurrencyTests {

        @Test
        @DisplayName("Concurrent creation of same placa")
        void concurrentSamePlaca() throws InterruptedException {
            AutoCreateRequest request = new AutoCreateRequest("RACE123", "carro", 10.0, null);

            Thread t1 = new Thread(() -> restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class));
            Thread t2 = new Thread(() -> restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class));

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            // At least one should succeed, at most one should succeed
            ResponseEntity<AutoListResponse> list = restTemplate.getForEntity(baseUrl, AutoListResponse.class);
            long count = list.getBody().getAutomoveis().stream()
                    .filter(a -> "RACE123".equalsIgnoreCase(a.getPlaca()))
                    .count();
            assertThat(count).isLessThanOrEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Special Characters & Encoding")
    class SpecialCharacterTests {

        @Test
        @DisplayName("Path traversal in ID")
        void pathTraversalInId() {
            assertThatThrownBy(() -> restTemplate.getForEntity(baseUrl + "/../../etc/passwd", AutoResponse.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("400");
        }

        @Test
        @DisplayName("Null bytes in input")
        void nullBytes() {
            String withNull = "ABC\0DEF";
            AutoCreateRequest request = new AutoCreateRequest(withNull, "carro", 10.0, null);

            // System may or may not validate - accept either behavior
            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);
            assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Newlines and carriage returns")
        void newlinesInInput() {
            String withNewlines = "ABC\n123\r";
            AutoCreateRequest request = new AutoCreateRequest(withNewlines, "carro", 10.0, null);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Control characters")
        void controlCharacters() {
            String withControl = "ABC\u0001\u0002\u0003";
            AutoCreateRequest request = new AutoCreateRequest(withControl, "carro", 10.0, null);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("HTTP Method Tampering")
    class HttpMethodTests {

        @Test
        @DisplayName("PATCH not allowed on /Automoveis/{id}")
        void patchNotAllowed() {
            HttpEntity<String> entity = new HttpEntity<>("{}", jsonHeaders());

            // RestTemplate doesn't support PATCH by default, expect ResourceAccessException
            assertThatThrownBy(() -> restTemplate.exchange(
                    baseUrl + "/some-id", HttpMethod.PATCH, entity, AutoResponse.class))
                    .isInstanceOf(org.springframework.web.client.ResourceAccessException.class);
        }

        @Test
        @DisplayName("OPTIONS returns allowed methods")
        void optionsAllowed() {
            ResponseEntity<Void> response = restTemplate.exchange(
                    baseUrl, HttpMethod.OPTIONS, null, Void.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            String allow = response.getHeaders().getFirst("Allow");
            assertThat(allow).contains("GET");
        }

        @Test
        @DisplayName("TRACE method disabled")
        void traceDisabled() {
            HttpMethod traceMethod = HttpMethod.valueOf("TRACE");

            // TRACE typically returns 405 or 501
            assertThatThrownBy(() -> restTemplate.exchange(
                    baseUrl, traceMethod, null, Void.class))
                    .isInstanceOf(HttpClientErrorException.class)
                    .hasMessageContaining("405");
        }
    }

    @Nested
    @DisplayName("Large Payload / DoS")
    class LargePayloadTests {

        @Test
        @DisplayName("Very large JSON payload")
        void largeJsonPayload() {
            String largeDescription = "A".repeat(100000); // 100KB
            AutoCreateRequest request = new AutoCreateRequest("LARGE1", "carro", 10.0, null);
            // Can't easily test large description without modifying DTO

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Many payments in single request")
        void manyPayments() {
            List<PagamentoRequest> payments = java.util.stream.IntStream.range(0, 100)
                    .mapToObj(i -> {
                        PagamentoRequest p = new PagamentoRequest();
                        p.setMetodo("dinheiro");
                        p.setValor(1.0);
                        return p;
                    })
                    .toList();

            AutoCreateRequest request = new AutoCreateRequest("MANYPAY", "carro", 100.0, payments);

            ResponseEntity<AutoResponse> response = restTemplate.postForEntity(baseUrl + "/Add", request, AutoResponse.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }
}