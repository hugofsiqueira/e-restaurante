package br.com.fiap.erestaurante.infra.web;

import br.com.fiap.erestaurante.core.controller.RestauranteController;
import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.HorarioFuncionamento;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.DonoRestauranteInvalidoException;
import br.com.fiap.erestaurante.core.exception.RestauranteNaoEncontradoException;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.infra.web.json.EnderecoResponse;
import br.com.fiap.erestaurante.infra.web.json.HorarioFuncionamentoResponse;
import br.com.fiap.erestaurante.infra.web.json.RestauranteResponse;
import br.com.fiap.erestaurante.infra.web.mapper.RestauranteWebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class RestauranteApiTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private RestauranteController restauranteController;

    @MockitoBean
    private RestauranteWebMapper mapper;

    private Restaurante restauranteDomain;
    private RestauranteResponse restauranteResponse;

    private static final String REQUEST_JSON = """
            {
              "nome": "Cantina da Maria",
              "donoId": 1,
              "tipoCozinha": "Italiana",
              "endereco": {
                "logradouro": "Rua das Flores",
                "numero": "10",
                "bairro": "Centro",
                "cidade": "São Paulo",
                "estado": "SP",
                "cep": "01310-100"
              },
              "horarioFuncionamento": {
                "abertura": "11:00",
                "fechamento": "23:00"
              }
            }
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        restauranteDomain = Restaurante.builder()
                .id(1L).nome("Cantina da Maria").tipoCozinha("Italiana")
                .dono(DonoRestaurante.builder().id(1L).nome("Maria Souza").build())
                .endereco(Endereco.builder().logradouro("Rua das Flores").numero("10")
                        .bairro("Centro").cidade("São Paulo").estado("SP").cep("01310-100").build())
                .horarioFuncionamento(HorarioFuncionamento.builder()
                        .abertura(LocalTime.of(11, 0)).fechamento(LocalTime.of(23, 0)).build())
                .build();

        var horarioResponse = new HorarioFuncionamentoResponse();
        horarioResponse.setAbertura(LocalTime.of(11, 0));
        horarioResponse.setFechamento(LocalTime.of(23, 0));

        var enderecoResponse = new EnderecoResponse();
        enderecoResponse.setLogradouro("Rua das Flores");
        enderecoResponse.setNumero("10");
        enderecoResponse.setBairro("Centro");
        enderecoResponse.setCidade("São Paulo");
        enderecoResponse.setEstado("SP");
        enderecoResponse.setCep("01310-100");

        restauranteResponse = new RestauranteResponse();
        restauranteResponse.setId(1L);
        restauranteResponse.setNome("Cantina da Maria");
        restauranteResponse.setTipoCozinha("Italiana");
        restauranteResponse.setDonoId(1L);
        restauranteResponse.setDonoNome("Maria Souza");
        restauranteResponse.setHorarioFuncionamento(horarioResponse);
        restauranteResponse.setEndereco(enderecoResponse);
    }

    // ── POST /v1/restaurantes ─────────────────────────────────────────────

    @Test
    void deveCriarRestauranteERetornar201() throws Exception {
        when(mapper.toCreateCommand(any())).thenReturn(new br.com.fiap.erestaurante.core.usecase.restaurante.CriarRestauranteUseCase.Command(
                "Cantina da Maria", 1L, "Italiana",
                "Rua das Flores", "10", null, "Centro", "São Paulo", "SP", "01310-100",
                LocalTime.of(11, 0), LocalTime.of(23, 0)
        ));
        when(restauranteController.criar(any())).thenReturn(restauranteDomain);
        when(mapper.toResponse(restauranteDomain)).thenReturn(restauranteResponse);

        mockMvc.perform(post("/v1/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Cantina da Maria"))
                .andExpect(jsonPath("$.tipoCozinha").value("Italiana"))
                .andExpect(jsonPath("$.donoId").value(1));
    }

    @Test
    void deveRetornar404SeDonoNaoEncontrado() throws Exception {
        when(mapper.toCreateCommand(any())).thenReturn(new br.com.fiap.erestaurante.core.usecase.restaurante.CriarRestauranteUseCase.Command(
                "Cantina", 99L, "Italiana",
                "Rua", "1", null, "Bairro", "Cidade", "SP", "00000-000",
                LocalTime.of(10, 0), LocalTime.of(22, 0)
        ));
        when(restauranteController.criar(any())).thenThrow(new UsuarioNaoEncontradoException(99L));

        mockMvc.perform(post("/v1/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    @Test
    void deveRetornar400SeUsuarioNaoEhDono() throws Exception {
        when(mapper.toCreateCommand(any())).thenReturn(new br.com.fiap.erestaurante.core.usecase.restaurante.CriarRestauranteUseCase.Command(
                "Cantina", 2L, "Italiana",
                "Rua", "1", null, "Bairro", "Cidade", "SP", "00000-000",
                LocalTime.of(10, 0), LocalTime.of(22, 0)
        ));
        when(restauranteController.criar(any())).thenThrow(new DonoRestauranteInvalidoException(2L));

        mockMvc.perform(post("/v1/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Regra de negócio violada"));
    }

    @Test
    void deveRetornar422SeRequestInvalido() throws Exception {
        mockMvc.perform(post("/v1/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "tipoCozinha": ""
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Dados inválidos"));
    }

    // ── GET /v1/restaurantes ──────────────────────────────────────────────

    @Test
    void deveBuscarPorNomeERetornar200() throws Exception {
        when(restauranteController.buscarPorNome("Cantina")).thenReturn(List.of(restauranteDomain));
        when(mapper.toResponseList(any())).thenReturn(List.of(restauranteResponse));

        mockMvc.perform(get("/v1/restaurantes").param("nome", "Cantina"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Cantina da Maria"));
    }

    @Test
    void deveRetornarListaVaziaSeNenhumRestauranteEncontrado() throws Exception {
        when(restauranteController.buscarPorNome("Inexistente")).thenReturn(List.of());
        when(mapper.toResponseList(any())).thenReturn(List.of());

        mockMvc.perform(get("/v1/restaurantes").param("nome", "Inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /v1/restaurantes/{id} ─────────────────────────────────────────

    @Test
    void deveBuscarPorIdERetornar200() throws Exception {
        when(restauranteController.buscarPorId(1L)).thenReturn(restauranteDomain);
        when(mapper.toResponse(restauranteDomain)).thenReturn(restauranteResponse);

        mockMvc.perform(get("/v1/restaurantes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Cantina da Maria"));
    }

    @Test
    void deveRetornar404SeRestauranteNaoEncontrado() throws Exception {
        when(restauranteController.buscarPorId(99L))
                .thenThrow(new RestauranteNaoEncontradoException(99L));

        mockMvc.perform(get("/v1/restaurantes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    // ── PUT /v1/restaurantes/{id} ─────────────────────────────────────────

    @Test
    void deveAtualizarRestauranteERetornar200() throws Exception {
        when(mapper.toUpdateCommand(any())).thenReturn(new br.com.fiap.erestaurante.core.usecase.restaurante.AtualizarRestauranteUseCase.Command(
                "Cantina da Maria", "Italiana",
                "Rua das Flores", "10", null, "Centro", "São Paulo", "SP", "01310-100",
                LocalTime.of(11, 0), LocalTime.of(23, 0)
        ));
        when(restauranteController.atualizar(eq(1L), any())).thenReturn(restauranteDomain);
        when(mapper.toResponse(restauranteDomain)).thenReturn(restauranteResponse);

        mockMvc.perform(put("/v1/restaurantes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveRetornar404AoAtualizarRestauranteInexistente() throws Exception {
        when(mapper.toUpdateCommand(any())).thenReturn(new br.com.fiap.erestaurante.core.usecase.restaurante.AtualizarRestauranteUseCase.Command(
                "Qualquer", "Italiana",
                "Rua", "1", null, "Bairro", "Cidade", "SP", "00000-000",
                LocalTime.of(10, 0), LocalTime.of(22, 0)
        ));
        when(restauranteController.atualizar(eq(99L), any()))
                .thenThrow(new RestauranteNaoEncontradoException(99L));

        mockMvc.perform(put("/v1/restaurantes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    // ── DELETE /v1/restaurantes/{id} ──────────────────────────────────────

    @Test
    void deveExcluirRestauranteERetornar204() throws Exception {
        doNothing().when(restauranteController).excluir(1L);

        mockMvc.perform(delete("/v1/restaurantes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoExcluirRestauranteInexistente() throws Exception {
        doThrow(new RestauranteNaoEncontradoException(99L))
                .when(restauranteController).excluir(99L);

        mockMvc.perform(delete("/v1/restaurantes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }
}
