package br.com.fiap.erestaurante.infra.web;

import br.com.fiap.erestaurante.core.controller.ItemCardapioController;
import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.ItemCardapioNaoEncontradoException;
import br.com.fiap.erestaurante.core.exception.RestauranteNaoEncontradoException;
import br.com.fiap.erestaurante.infra.web.json.ItemCardapioResponse;
import br.com.fiap.erestaurante.infra.web.mapper.ItemCardapioWebMapper;
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

import java.math.BigDecimal;
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
class ItemCardapioApiTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private ItemCardapioController itemCardapioController;

    @MockitoBean
    private ItemCardapioWebMapper mapper;

    private ItemCardapio itemDomain;
    private ItemCardapioResponse itemResponse;

    private static final String REQUEST_JSON = """
            {
              "nome": "Pizza Margherita",
              "descricao": "Clássica pizza italiana",
              "preco": 35.90,
              "disponivel": true,
              "fotoPath": "/fotos/pizza.jpg",
              "restauranteId": 1
            }
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        itemDomain = ItemCardapio.builder()
                .id(1L).nome("Pizza Margherita").descricao("Clássica pizza italiana")
                .preco(new BigDecimal("35.90")).disponivel(true).fotoPath("/fotos/pizza.jpg")
                .restaurante(Restaurante.builder().id(1L).nome("Cantina da Maria").build())
                .build();

        itemResponse = new ItemCardapioResponse();
        itemResponse.setId(1L);
        itemResponse.setNome("Pizza Margherita");
        itemResponse.setDescricao("Clássica pizza italiana");
        itemResponse.setPreco(new BigDecimal("35.90"));
        itemResponse.setDisponivel(true);
        itemResponse.setFotoPath("/fotos/pizza.jpg");
        itemResponse.setRestauranteId(1L);
        itemResponse.setRestauranteNome("Cantina da Maria");
    }

    // ── POST /v1/itens-cardapio ───────────────────────────────────────────

    @Test
    void deveAdicionarItemERetornar201() throws Exception {
        when(mapper.toAddCommand(any())).thenReturn(new br.com.fiap.erestaurante.core.usecase.itemcardapio.AdicionarItemCardapioUseCase.Command(
                "Pizza Margherita", "Clássica pizza italiana",
                new BigDecimal("35.90"), true, "/fotos/pizza.jpg", 1L
        ));
        when(itemCardapioController.adicionar(any())).thenReturn(itemDomain);
        when(mapper.toResponse(itemDomain)).thenReturn(itemResponse);

        mockMvc.perform(post("/v1/itens-cardapio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Pizza Margherita"))
                .andExpect(jsonPath("$.preco").value(35.90))
                .andExpect(jsonPath("$.restauranteId").value(1));
    }

    @Test
    void deveRetornar404SeRestauranteNaoEncontrado() throws Exception {
        when(mapper.toAddCommand(any())).thenReturn(new br.com.fiap.erestaurante.core.usecase.itemcardapio.AdicionarItemCardapioUseCase.Command(
                "Pizza", "Desc", new BigDecimal("20.00"), true, null, 99L
        ));
        when(itemCardapioController.adicionar(any())).thenThrow(new RestauranteNaoEncontradoException(99L));

        mockMvc.perform(post("/v1/itens-cardapio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    @Test
    void deveRetornar422SeRequestInvalido() throws Exception {
        mockMvc.perform(post("/v1/itens-cardapio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "preco": -1.00
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Dados inválidos"));
    }

    // ── GET /v1/itens-cardapio?restauranteId=1 ────────────────────────────

    @Test
    void deveBuscarPorRestauranteERetornar200() throws Exception {
        when(itemCardapioController.buscarPorRestaurante(1L)).thenReturn(List.of(itemDomain));
        when(mapper.toResponseList(any())).thenReturn(List.of(itemResponse));

        mockMvc.perform(get("/v1/itens-cardapio").param("restauranteId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Pizza Margherita"));
    }

    @Test
    void deveRetornarListaVaziaSeRestauranteSemItens() throws Exception {
        when(itemCardapioController.buscarPorRestaurante(2L)).thenReturn(List.of());
        when(mapper.toResponseList(any())).thenReturn(List.of());

        mockMvc.perform(get("/v1/itens-cardapio").param("restauranteId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /v1/itens-cardapio/{id} ───────────────────────────────────────

    @Test
    void deveBuscarPorIdERetornar200() throws Exception {
        when(itemCardapioController.buscarPorId(1L)).thenReturn(itemDomain);
        when(mapper.toResponse(itemDomain)).thenReturn(itemResponse);

        mockMvc.perform(get("/v1/itens-cardapio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Pizza Margherita"));
    }

    @Test
    void deveRetornar404SeItemNaoEncontrado() throws Exception {
        when(itemCardapioController.buscarPorId(99L))
                .thenThrow(new ItemCardapioNaoEncontradoException(99L));

        mockMvc.perform(get("/v1/itens-cardapio/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    // ── PUT /v1/itens-cardapio/{id} ───────────────────────────────────────

    @Test
    void deveAtualizarItemERetornar200() throws Exception {
        when(mapper.toUpdateCommand(any())).thenReturn(new br.com.fiap.erestaurante.core.usecase.itemcardapio.AtualizarItemCardapioUseCase.Command(
                "Pizza Margherita", "Clássica pizza italiana",
                new BigDecimal("35.90"), true, "/fotos/pizza.jpg"
        ));
        when(itemCardapioController.atualizar(eq(1L), any())).thenReturn(itemDomain);
        when(mapper.toResponse(itemDomain)).thenReturn(itemResponse);

        mockMvc.perform(put("/v1/itens-cardapio/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveRetornar404AoAtualizarItemInexistente() throws Exception {
        when(mapper.toUpdateCommand(any())).thenReturn(new br.com.fiap.erestaurante.core.usecase.itemcardapio.AtualizarItemCardapioUseCase.Command(
                "Qualquer", "Desc", new BigDecimal("10.00"), true, null
        ));
        when(itemCardapioController.atualizar(eq(99L), any()))
                .thenThrow(new ItemCardapioNaoEncontradoException(99L));

        mockMvc.perform(put("/v1/itens-cardapio/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    // ── DELETE /v1/itens-cardapio/{id} ────────────────────────────────────

    @Test
    void deveExcluirItemERetornar204() throws Exception {
        doNothing().when(itemCardapioController).excluir(1L);

        mockMvc.perform(delete("/v1/itens-cardapio/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoExcluirItemInexistente() throws Exception {
        doThrow(new ItemCardapioNaoEncontradoException(99L))
                .when(itemCardapioController).excluir(99L);

        mockMvc.perform(delete("/v1/itens-cardapio/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }
}
