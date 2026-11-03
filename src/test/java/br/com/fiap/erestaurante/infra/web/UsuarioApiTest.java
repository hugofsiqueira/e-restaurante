package br.com.fiap.erestaurante.infra.web;

import br.com.fiap.erestaurante.core.controller.UsuarioController;
import br.com.fiap.erestaurante.core.domain.Cliente;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.exception.EmailJaCadastradoException;
import br.com.fiap.erestaurante.core.exception.SenhaInvalidaException;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.usecase.usuario.AtualizarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.CadastrarUsuarioUseCase;
import br.com.fiap.erestaurante.infra.web.json.AtualizarUsuarioRequest;
import br.com.fiap.erestaurante.infra.web.json.CadastrarUsuarioRequest;
import br.com.fiap.erestaurante.infra.web.json.EnderecoResponse;
import br.com.fiap.erestaurante.infra.web.json.TipoUsuarioResponse;
import br.com.fiap.erestaurante.infra.web.json.UsuarioResponse;
import br.com.fiap.erestaurante.infra.web.mapper.UsuarioWebMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class UsuarioApiTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioController usuarioController;

    @MockitoBean
    private UsuarioWebMapper mapper;

    private UsuarioResponse usuarioResponse;
    private Usuario clienteDomain;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        var tipoCliente = TipoUsuario.builder().id(1L).nome("CLIENTE").build();
        var endereco = Endereco.builder()
                .logradouro("Rua das Flores").numero("123").bairro("Centro")
                .cidade("São Paulo").estado("SP").cep("01310-100").build();
        clienteDomain = Cliente.builder()
                .id(1L).nome("João Silva").email("joao@email.com")
                .login("joaosilva").senha("hash")
                .endereco(endereco).tipoUsuario(tipoCliente).build();

        var enderecoResponse = new EnderecoResponse();
        enderecoResponse.setLogradouro("Rua das Flores");
        enderecoResponse.setNumero("123");
        enderecoResponse.setBairro("Centro");
        enderecoResponse.setCidade("São Paulo");
        enderecoResponse.setEstado("SP");
        enderecoResponse.setCep("01310-100");

        var tipoResponse = new TipoUsuarioResponse();
        tipoResponse.setId(1L);
        tipoResponse.setNome("CLIENTE");

        usuarioResponse = new UsuarioResponse();
        usuarioResponse.setId(1L);
        usuarioResponse.setNome("João Silva");
        usuarioResponse.setEmail("joao@email.com");
        usuarioResponse.setLogin("joaosilva");
        usuarioResponse.setTipoUsuario(tipoResponse);
        usuarioResponse.setEndereco(enderecoResponse);
    }

    // ── POST /v1/usuarios ─────────────────────────────────────────────────

    @Test
    void deveCadastrarUsuarioERetornar201() throws Exception {
        when(mapper.toCommand(any(CadastrarUsuarioRequest.class)))
                .thenReturn(new CadastrarUsuarioUseCase.Command(
                        "João Silva", "joao@email.com", "joaosilva", "senha123",
                        1L,
                        "Rua das Flores", "123", null, "Centro", "São Paulo", "SP", "01310-100"
                ));
        when(usuarioController.cadastrar(any())).thenReturn(clienteDomain);
        when(mapper.toResponse(clienteDomain)).thenReturn(usuarioResponse);

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "João Silva",
                                  "email": "joao@email.com",
                                  "login": "joaosilva",
                                  "senha": "senha123",
                                  "tipoUsuarioId": 1,
                                  "endereco": {
                                    "logradouro": "Rua das Flores",
                                    "numero": "123",
                                    "bairro": "Centro",
                                    "cidade": "São Paulo",
                                    "estado": "SP",
                                    "cep": "01310-100"
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.tipoUsuario.nome").value("CLIENTE"));
    }

    @Test
    void deveRetornar409SeEmailJaCadastrado() throws Exception {
        when(mapper.toCommand(any(CadastrarUsuarioRequest.class)))
                .thenReturn(new CadastrarUsuarioUseCase.Command(
                        "Dup", "dup@email.com", "dup", "senha123",
                        1L,
                        "Rua", "1", null, "Bairro", "Cidade", "SP", "01000-000"
                ));
        when(usuarioController.cadastrar(any())).thenThrow(new EmailJaCadastradoException("dup@email.com"));

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Dup",
                                  "email": "dup@email.com",
                                  "login": "dup",
                                  "senha": "senha123",
                                  "tipoUsuarioId": 1,
                                  "endereco": {
                                    "logradouro": "Rua",
                                    "numero": "1",
                                    "bairro": "Bairro",
                                    "cidade": "Cidade",
                                    "estado": "SP",
                                    "cep": "01000-000"
                                  }
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"));
    }

    @Test
    void deveRetornar422SeRequestInvalido() throws Exception {
        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "email": "email-invalido",
                                  "login": "",
                                  "senha": "123",
                                  "tipoUsuarioId": 1,
                                  "endereco": {
                                    "logradouro": "Rua",
                                    "numero": "1",
                                    "bairro": "Bairro",
                                    "cidade": "Cidade",
                                    "estado": "SP",
                                    "cep": "01000-000"
                                  }
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Dados inválidos"));
    }

    // ── GET /v1/usuarios?nome=... ──────────────────────────────────────────

    @Test
    void deveBuscarUsuariosPorNomeERetornar200() throws Exception {
        when(usuarioController.buscarPorNome("João")).thenReturn(List.of(clienteDomain));
        when(mapper.toResponseList(any())).thenReturn(List.of(usuarioResponse));

        mockMvc.perform(get("/v1/usuarios").param("nome", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    void deveRetornarListaVaziaSeNenhumUsuarioEncontrado() throws Exception {
        when(usuarioController.buscarPorNome("Inexistente")).thenReturn(List.of());
        when(mapper.toResponseList(any())).thenReturn(List.of());

        mockMvc.perform(get("/v1/usuarios").param("nome", "Inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── PUT /v1/usuarios/{id} ─────────────────────────────────────────────

    @Test
    void deveAtualizarUsuarioERetornar200() throws Exception {
        when(mapper.toCommand(any(AtualizarUsuarioRequest.class)))
                .thenReturn(new AtualizarUsuarioUseCase.Command(
                        "João Atualizado", "joao@email.com",
                        "Rua das Flores", "123", null, "Centro", "São Paulo", "SP", "01310-100"
                ));
        when(usuarioController.atualizar(eq(1L), any())).thenReturn(clienteDomain);
        when(mapper.toResponse(clienteDomain)).thenReturn(usuarioResponse);

        mockMvc.perform(put("/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "João Atualizado",
                                  "email": "joao@email.com",
                                  "endereco": {
                                    "logradouro": "Rua das Flores",
                                    "numero": "123",
                                    "bairro": "Centro",
                                    "cidade": "São Paulo",
                                    "estado": "SP",
                                    "cep": "01310-100"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveRetornar404AoAtualizarUsuarioInexistente() throws Exception {
        when(mapper.toCommand(any(AtualizarUsuarioRequest.class)))
                .thenReturn(new AtualizarUsuarioUseCase.Command(
                        "Nome", "email@email.com",
                        "Rua", "1", null, "Bairro", "Cidade", "SP", "01000-000"
                ));
        when(usuarioController.atualizar(eq(99L), any())).thenThrow(new UsuarioNaoEncontradoException(99L));

        mockMvc.perform(put("/v1/usuarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Nome",
                                  "email": "email@email.com",
                                  "endereco": {
                                    "logradouro": "Rua",
                                    "numero": "1",
                                    "bairro": "Bairro",
                                    "cidade": "Cidade",
                                    "estado": "SP",
                                    "cep": "01000-000"
                                  }
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    // ── DELETE /v1/usuarios/{id} ──────────────────────────────────────────

    @Test
    void deveExcluirUsuarioERetornar204() throws Exception {
        doNothing().when(usuarioController).excluir(1L);

        mockMvc.perform(delete("/v1/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoExcluirUsuarioInexistente() throws Exception {
        doThrow(new UsuarioNaoEncontradoException(99L)).when(usuarioController).excluir(99L);

        mockMvc.perform(delete("/v1/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }

    // ── PATCH /v1/usuarios/{id}/senha ─────────────────────────────────────

    @Test
    void deveTrocarSenhaERetornar204() throws Exception {
        doNothing().when(usuarioController).trocarSenha(eq(1L), any());

        mockMvc.perform(patch("/v1/usuarios/1/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "senhaAtual": "senhaAtual123",
                                  "novaSenha": "novaSenha456"
                                }
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar400SeSenhaAtualIncorreta() throws Exception {
        doThrow(new SenhaInvalidaException()).when(usuarioController).trocarSenha(eq(1L), any());

        mockMvc.perform(patch("/v1/usuarios/1/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "senhaAtual": "senhaErrada",
                                  "novaSenha": "novaSenha456"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Regra de negócio violada"));
    }

    @Test
    void deveRetornar404AoTrocarSenhaDeUsuarioInexistente() throws Exception {
        doThrow(new UsuarioNaoEncontradoException(99L)).when(usuarioController).trocarSenha(eq(99L), any());

        mockMvc.perform(patch("/v1/usuarios/99/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "senhaAtual": "senhaAtual123",
                                  "novaSenha": "novaSenha456"
                                }
                                """))
                .andExpect(status().isNotFound());
    }
}
