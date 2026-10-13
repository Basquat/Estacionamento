package basquat.estacionamento.application.mapper;

import basquat.estacionamento.application.dto.request.AutoCreateRequest;
import basquat.estacionamento.application.dto.request.AutoUpdateRequest;
import basquat.estacionamento.application.dto.request.PagamentoRequest;
import basquat.estacionamento.domain.model.Auto;
import basquat.estacionamento.domain.model.Pagamento;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    @Test
    void updatePreservaIdEDataDePagamentoExistenteEIgnoraIdAlheio() {
        Auto auto = new Auto("a1", "ABC1234", "carro", 10.0, false, 1L, new java.util.ArrayList<>(List.of(
                new Pagamento("p1", "a1", "pix", 10.0, 0.0, 0.0, 111L))));

        PagamentoRequest existente = new PagamentoRequest("pix", 6.0, 0.0, 0.0);
        existente.setId("p1");
        PagamentoRequest alheio = new PagamentoRequest("dinheiro", 4.0, 0.0, 0.0);
        alheio.setId("de-outro-veiculo");

        Mapper.updateEntity(auto, new AutoUpdateRequest(null, null, null, true, List.of(existente, alheio)));

        assertEquals(2, auto.getPagamentos().size());
        assertEquals("p1", auto.getPagamentos().get(0).getId());
        assertEquals(111L, auto.getPagamentos().get(0).getData());
        assertNotEquals("de-outro-veiculo", auto.getPagamentos().get(1).getId());
        assertNull(auto.getPagamentos().get(1).getData());
    }

    @Test
    void criarComPagamentosNasceComoPagoEGuardaValorPrevisto() {
        AutoCreateRequest req = new AutoCreateRequest("ABC1234", "carro", 20.0,
                List.of(new PagamentoRequest("pix", 20.0, 0.0, 0.0)));
        req.setValorPrevisto(25.0);

        Auto auto = Mapper.toEntity(req);

        assertTrue(auto.getPago());
        assertEquals(25.0, auto.getValorPrevisto());
        assertEquals(20.0, auto.getValor());
    }

    @Test
    void criarSemPagamentosNasceComoPendente() {
        Auto auto = Mapper.toEntity(new AutoCreateRequest("ABC1234", "carro", 20.0, null));

        assertFalse(auto.getPago());
        assertNull(auto.getValorPrevisto());
    }
}
