package basquat.estacionamento.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Fluxo completo da API (controller -> service -> adapter JPA -> H2), usando
 * MockMvc: não abre porta/socket, então roda em qualquer máquina.
 */
@SpringBootTest
@ActiveProfiles("test")
class FluxoCompletoMockMvcTest {

    @Autowired
    WebApplicationContext ctx;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    private String criar(String json) throws Exception {
        MvcResult r = mvc.perform(post("/Automoveis/Add").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andReturn();
        return r.getResponse().getContentAsString();
    }

    private String lerPorId(String id) throws Exception {
        return mvc.perform(get("/Automoveis/" + id)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void prefixoApiResponde_eLiberaCorsDoApk() throws Exception {
        mvc.perform(get("/api/Automoveis")).andExpect(status().isOk());
        mvc.perform(get("/api/Blacklist")).andExpect(status().isOk());
        mvc.perform(options("/api/Automoveis")
                        .header("Origin", "https://localhost")
                        .header("Access-Control-Request-Method", "PUT"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://localhost"));
        mvc.perform(get("/Automoveis")).andExpect(status().isOk());
    }

    @Test
    void listaVeiculoPendenteNoFormatoNovo() throws Exception {
        String id = JsonPath.read(criar("{\"placa\":\"LST0001\",\"tipo\":\"carro\",\"valor\":25}"), "$.id");

        mvc.perform(get("/Automoveis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.automoveis").isArray())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.automoveis[?(@.id=='" + id + "')].pago").value(false));
    }

    @Test
    void pagamentoComTrocoEValorPrevistoEPersistidoELido() throws Exception {
        String criado = criar("{\"placa\":\"PAG0001\",\"tipo\":\"carro\",\"valor\":25,\"valorPrevisto\":30,"
                + "\"pagamentos\":[{\"metodo\":\"pix\",\"valor\":50,\"trocoDinheiro\":25,\"trocoPix\":0}]}");

        assertThat((Boolean) JsonPath.read(criado, "$.pago")).isTrue();
        String id = JsonPath.read(criado, "$.id");

        String lido = lerPorId(id);
        assertThat((Double) JsonPath.read(lido, "$.valor")).isEqualTo(25.0);
        assertThat((Double) JsonPath.read(lido, "$.valorPrevisto")).isEqualTo(30.0);
        assertThat((String) JsonPath.read(lido, "$.pagamentos[0].metodo")).isEqualTo("pix");
        assertThat((Double) JsonPath.read(lido, "$.pagamentos[0].valor")).isEqualTo(50.0);
        assertThat((Double) JsonPath.read(lido, "$.pagamentos[0].trocoDinheiro")).isEqualTo(25.0);
        assertThat((Double) JsonPath.read(lido, "$.pagamentos[0].trocoPix")).isEqualTo(0.0);
    }

    @Test
    void trocoSeparadoEmDinheiroEPix() throws Exception {
        String id = JsonPath.read(criar("{\"placa\":\"TRC0001\",\"tipo\":\"moto\",\"valor\":20,"
                + "\"pagamentos\":[{\"metodo\":\"dinheiro\",\"valor\":50,\"trocoDinheiro\":10,\"trocoPix\":20}]}"), "$.id");

        String lido = lerPorId(id);
        assertThat((Double) JsonPath.read(lido, "$.pagamentos[0].trocoDinheiro")).isEqualTo(10.0);
        assertThat((Double) JsonPath.read(lido, "$.pagamentos[0].trocoPix")).isEqualTo(20.0);
    }

    @Test
    void editarPreservaIdEDataDoPagamentoEIgnoraIdAlheio() throws Exception {
        String id = JsonPath.read(criar("{\"placa\":\"EDT0001\",\"tipo\":\"carro\",\"valor\":25,"
                + "\"pagamentos\":[{\"metodo\":\"pix\",\"valor\":25}]}"), "$.id");
        String antes = lerPorId(id);
        String pid = JsonPath.read(antes, "$.pagamentos[0].id");
        Number dataAntes = JsonPath.read(antes, "$.pagamentos[0].data");

        String body = "{\"valor\":25,\"valorPrevisto\":25,\"pago\":true,\"pagamentos\":["
                + "{\"id\":\"" + pid + "\",\"metodo\":\"pix\",\"valor\":30,\"trocoDinheiro\":5},"
                + "{\"id\":\"id-de-outro-veiculo\",\"metodo\":\"dinheiro\",\"valor\":10}]}";
        String depois = mvc.perform(put("/Automoveis/" + id).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThat((String) JsonPath.read(depois, "$.pagamentos[0].id")).isEqualTo(pid);
        assertThat(((Number) JsonPath.read(depois, "$.pagamentos[0].data")).longValue()).isEqualTo(dataAntes.longValue());
        assertThat((Double) JsonPath.read(depois, "$.pagamentos[0].trocoDinheiro")).isEqualTo(5.0);
        assertThat((String) JsonPath.read(depois, "$.pagamentos[1].id")).isNotEqualTo("id-de-outro-veiculo");
        // e continua assim ao reler do banco
        assertThat(((Number) JsonPath.read(lerPorId(id), "$.pagamentos[0].data")).longValue()).isEqualTo(dataAntes.longValue());
    }

    @Test
    void desmarcarPagoLimpaPagamentos() throws Exception {
        String id = JsonPath.read(criar("{\"placa\":\"UND0001\",\"tipo\":\"carro\",\"valor\":25,"
                + "\"pagamentos\":[{\"metodo\":\"pix\",\"valor\":25}]}"), "$.id");

        mvc.perform(put("/Automoveis/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pago\":false,\"pagamentos\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pago").value(false))
                .andExpect(jsonPath("$.pagamentos").isEmpty());
    }

    @Test
    void excluirRemoveVeiculoEPagamentos() throws Exception {
        String id = JsonPath.read(criar("{\"placa\":\"DEL0001\",\"tipo\":\"carro\",\"valor\":25,"
                + "\"pagamentos\":[{\"metodo\":\"pix\",\"valor\":25}]}"), "$.id");

        mvc.perform(delete("/Automoveis/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/Automoveis/" + id)).andExpect(status().isNotFound());
        mvc.perform(delete("/Automoveis/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void placaDuplicadaSemDiferenciarMaiusculaERejeitada() throws Exception {
        criar("{\"placa\":\"DUP0001\",\"tipo\":\"carro\",\"valor\":25}");

        mvc.perform(post("/Automoveis/Add").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placa\":\"dup0001\",\"tipo\":\"moto\",\"valor\":10}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void validacoesDeEntrada() throws Exception {
        mvc.perform(post("/Automoveis/Add").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placa\":\"\",\"tipo\":\"carro\",\"valor\":25}"))
                .andExpect(status().is4xxClientError());
        mvc.perform(post("/Automoveis/Add").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placa\":\"VAL0001\",\"tipo\":\"carro\",\"valor\":0}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void blacklistAdicionarListarRemover() throws Exception {
        MvcResult r = mvc.perform(post("/Blacklist/Add").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placa\":\"BLK0001\",\"motivo\":\"saiu sem pagar\"}"))
                .andExpect(status().isOk()).andReturn();
        String id = JsonPath.read(r.getResponse().getContentAsString(), "$.id");

        mvc.perform(get("/Blacklist")).andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id=='" + id + "')].placa").value("BLK0001"));
        mvc.perform(delete("/Blacklist/" + id)).andExpect(status().isNoContent());
        mvc.perform(delete("/Blacklist/" + id)).andExpect(status().isNotFound());
    }
}
