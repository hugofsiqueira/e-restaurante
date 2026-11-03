package br.com.fiap.erestaurante.infra.web;

import br.com.fiap.erestaurante.core.controller.TipoUsuarioController;
import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioJaCadastradoException;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.infra.web.json.TipoUsuarioResponse;
import br.com.fiap.erestaurante.infra.web.mapper.TipoUsuarioWebMapper;
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
class TipoUsuarioApiTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private TipoUsuarioController tipoUsuarioController;

    @MockitoBean
    private TipoUsuarioWebMapper mapper;

    private TipoUsuario clienteTipo;
    private TipoUsuarioResponse clienteResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        clienteTipo = TipoUsuario.builder().id(1L).nome("CLIENTE").build();

        clienteResponse = new TipoUsuarioResponse();
        clienteResponse.setId(1L);
        clienteResponse.setNome("CLIENTE");
    }

    // ── POST /v1/tipos-usuario ────────────────────────────────────────────

    @Test
    void deveCriarTipoUsuarioERetornar201() throws Exception {
        when(tipoUsuarioController.criar(any())).thenReturn(clienteTipo);
        when(mapper.toResponse(clienteTipo)).thenReturn(clienteResponse);

        mockMvc.perform(post("/v1/tipos-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nome": "CLIENTE" }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("CLIENTE"));
    }

    @Test
    void deveRetornar409SeTipoJaCadastrado() throws Exception {
        when(tipoUsuarioController.criar(any()))
                .thenThrow(new TipoUsuarioJaCadastradoException("CLIENTE"));

        mockMvc.perform(post("/v1/tipos-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nome": "CLIENTE" }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"));
    }

    @Test
    void deveRetornar422SeNomeEmBranco() throws Exception {
        mockMvc.perform(post("/v1/tipos-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nome": "" }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Dados inválidos"));
    }

    // ── GET /v1/tipos-usuario ─────────────────────────────────────────────

    @Test
    void deveListarTiposERetornar200() throws Exception {
        var donoTipo = TipoUsuario.builder().id(2L).nome("DONO_RESTAURANTE").build();
        var donoResponse = new TipoUsuarioResponse();
        donoResponse.setId(2L);
        donoResponse.setNome("DONO_RESTAURANTE");

        when(tipoUsuarioController.buscarTodos()).thenReturn(List.of(clienteTipo, donoTipo));
        when(mapper.toResponseList(any())).thenReturn(List.of(clienteResponse, donoResponse));

        mockMvc.perform(get("/v1/tipos-usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("CLIENTE"));
    }

    // ── GET /v1/tipos-usuario/{id} ────────────────────────────────────────

    @Test
    void deveBuscarPorIdERetornar200() throws Exception {
        when(tipoUsuarioController.buscarPorId(1L)).thenReturn(clienteTipo);
        when(mapper.toResponse(clienteTipo)).thenReturn(clienteResponse);

        mockMvc.perform(get("/v1/tipos-usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("CLIENTE"));
    }

    @Test
    void deveRetornar404SeTipoNaoEncontrado() throws Exception {
        when(tipoUsuarioController.buscarPorId(99L))
                .thenThrow(new TipoUsuarioNaoEncontradoException(99L));

        mockMvc.perform(get("/v1/tipos-usuario/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    // ── PUT /v1/tipos-usuario/{id} ────────────────────────────────────────

    @Test
    void deveAtualizarTipoUsuarioERetornar200() throws Exception {
        var atualizado = TipoUsuario.builder().id(1L).nome("CLIENTE_VIP").build();
        var atualizadoResponse = new TipoUsuarioResponse();
        atualizadoResponse.setId(1L);
        atualizadoResponse.setNome("CLIENTE_VIP");

        when(tipoUsuarioController.atualizar(eq(1L), any())).thenReturn(atualizado);
        when(mapper.toResponse(atualizado)).thenReturn(atualizadoResponse);

        mockMvc.perform(put("/v1/tipos-usuario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nome": "CLIENTE_VIP" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("CLIENTE_VIP"));
    }

    // ── DELETE /v1/tipos-usuario/{id} ─────────────────────────────────────

    @Test
    void deveExcluirTipoUsuarioERetornar204() throws Exception {
        doNothing().when(tipoUsuarioController).excluir(1L);

        mockMvc.perform(delete("/v1/tipos-usuario/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoExcluirTipoNaoEncontrado() throws Exception {
        doThrow(new TipoUsuarioNaoEncontradoException(99L))
                .when(tipoUsuarioController).excluir(99L);

        mockMvc.perform(delete("/v1/tipos-usuario/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }
}
