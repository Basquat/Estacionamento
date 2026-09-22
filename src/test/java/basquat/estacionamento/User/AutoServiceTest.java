package basquat.estacionamento.User;

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
    private AutoRepository autoRepository;
    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private EventoService eventoService;

    private AutoService service;

    @BeforeEach
    void setUp() {
        service = new AutoService(autoRepository, pagamentoRepository, eventoService);
    }

    private AutoModel carro(String placa) {
        AutoModel model = new AutoModel();
        model.setId("id-1");
        model.setPlaca(placa);
        model.setTipo("carro");
        model.setValor(10.0);
        model.setPago(false);
        model.setEntrada(System.currentTimeMillis());
        return model;
    }

    @Test
    void criarSemPlacaLancaRegraNegocio() {
        AutoModel model = carro(null);

        assertThatThrownBy(() -> service.criar(model))
                .isInstanceOf(RegraNegocioException.class);

        verifyNoInteractions(autoRepository, eventoService);
    }

    @Test
    void criarComPlacaJaCadastradaLancaRegraNegocio() {
        AutoModel model = carro("ABC1234");
        when(autoRepository.existsByPlacaIgnoreCase("ABC1234")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(model))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("ABC1234");

        verify(autoRepository, never()).save(any());
        verifyNoInteractions(eventoService);
    }

    @Test
    void criarComPlacaNovaSalvaENotifica() {
        AutoModel model = carro("XYZ9999");
        when(autoRepository.existsByPlacaIgnoreCase("XYZ9999")).thenReturn(false);
        when(autoRepository.save(model)).thenReturn(model);

        AutoModel salvo = service.criar(model);

        assertThat(salvo).isSameAs(model);
        verify(autoRepository).save(model);
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
        AutoModel existente = carro("AAA1111");
        AutoModel detalhe = carro("BBB2222");
        when(autoRepository.findById("id-1")).thenReturn(Optional.of(existente));
        when(autoRepository.existsByPlacaIgnoreCase("BBB2222")).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar("id-1", detalhe))
                .isInstanceOf(RegraNegocioException.class);

        verify(autoRepository, never()).save(any());
    }

    @Test
    void removerInexistenteLancaRecursoNaoEncontrado() {
        when(autoRepository.deleteByIdReturningCount("sumiu")).thenReturn(0);

        assertThatThrownBy(() -> service.remover("sumiu"))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(pagamentoRepository).deleteByAutoId("sumiu");
        verifyNoInteractions(eventoService);
    }

    @Test
    void removerExistenteApagaPagamentosENotifica() {
        when(autoRepository.deleteByIdReturningCount("id-1")).thenReturn(1);

        service.remover("id-1");

        verify(pagamentoRepository).deleteByAutoId("id-1");
        verify(eventoService).notificar();
    }
}
