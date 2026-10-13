package basquat.estacionamento.application.service;

import basquat.estacionamento.application.dto.request.AutoCreateRequest;
import basquat.estacionamento.application.dto.request.AutoUpdateRequest;
import basquat.estacionamento.application.dto.request.PagamentoRequest;
import basquat.estacionamento.application.dto.response.AutoResponse;
import basquat.estacionamento.application.port.out.AutoRepositoryPort;
import basquat.estacionamento.application.port.out.PagamentoRepositoryPort;
import basquat.estacionamento.application.service.AutoServiceImpl;
import basquat.estacionamento.domain.exception.RecursoNaoEncontradoException;
import basquat.estacionamento.domain.exception.RegraNegocioException;
import basquat.estacionamento.domain.model.Auto;
import basquat.estacionamento.infrastructure.event.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoServiceTest {

    @Mock
    private AutoRepositoryPort autoRepository;
    @Mock
    private PagamentoRepositoryPort pagamentoRepository;
    @Mock
    private EventoService eventoService;

    private AutoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AutoServiceImpl(autoRepository, pagamentoRepository, eventoService);
    }

    private AutoCreateRequest createRequest(String placa) {
        return new AutoCreateRequest(placa, "carro", 10.0, null);
    }

    private AutoUpdateRequest updateRequest(String placa) {
        return new AutoUpdateRequest(placa, "carro", 15.0, true, null);
    }

    @Test
    void criarSemPlacaLancaRegraNegocio() {
        AutoCreateRequest request = createRequest(null);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class);

        verifyNoInteractions(autoRepository, eventoService);
    }

    @Test
    void criarComPlacaJaCadastradaLancaRegraNegocio() {
        AutoCreateRequest request = createRequest("ABC1234");
        when(autoRepository.existsByPlacaIgnoreCase("ABC1234")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("ABC1234");

        verify(autoRepository, never()).save(any());
        verifyNoInteractions(eventoService);
    }

    @Test
    void criarComPlacaNovaSalvaENotifica() {
        AutoCreateRequest request = createRequest("XYZ9999");
        when(autoRepository.existsByPlacaIgnoreCase("XYZ9999")).thenReturn(false);
        when(autoRepository.save(any(Auto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AutoResponse salvo = service.criar(request);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getPlaca()).isEqualTo("XYZ9999");
        verify(autoRepository).save(any(Auto.class));
        verify(eventoService).notificar();
    }

    @Test
    void buscarNaoEncontradoLancaRecursoNaoEncontrado() {
        when(autoRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar("inexistente"))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void atualizarComPlacaDeOutroVeiculoLancaRegraNegocio() {
        Auto existingAuto = new Auto();
        existingAuto.setId("id-1");
        existingAuto.setPlaca("AAA1111");
        existingAuto.setTipo("carro");
        existingAuto.setValor(10.0);
        existingAuto.setPago(false);
        existingAuto.setEntrada(System.currentTimeMillis());

        AutoUpdateRequest request = updateRequest("BBB2222");
        when(autoRepository.findById("id-1")).thenReturn(Optional.of(existingAuto));
        when(autoRepository.existsByPlacaIgnoreCase("BBB2222")).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar("id-1", request))
                .isInstanceOf(RegraNegocioException.class);

        verify(autoRepository, never()).save(any());
    }

@Test
    void removerInexistenteLancaRecursoNaoEncontrado() {
        when(autoRepository.findById("sumiu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.remover("sumiu"))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verifyNoInteractions(pagamentoRepository, eventoService);
    }

    @Test
    void removerExistenteApagaPagamentosENotifica() {
        Auto existingAuto = new Auto();
        existingAuto.setId("id-1");
        existingAuto.setPlaca("AAA1111");
        existingAuto.setTipo("carro");
        existingAuto.setValor(10.0);
        existingAuto.setPago(false);
        existingAuto.setEntrada(System.currentTimeMillis());

        when(autoRepository.findById("id-1")).thenReturn(Optional.of(existingAuto));
        when(autoRepository.deleteByIdReturningCount("id-1")).thenReturn(1);

        service.remover("id-1");

        verify(pagamentoRepository).deleteByAutoId("id-1");
        verify(eventoService).notificar();
    }
}