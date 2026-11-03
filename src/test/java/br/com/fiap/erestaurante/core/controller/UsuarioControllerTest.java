package br.com.fiap.erestaurante.core.controller;

import br.com.fiap.erestaurante.core.domain.Cliente;
import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.usecase.usuario.AtualizarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.AutenticarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.BuscarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.CadastrarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.ExcluirUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.TrocarSenhaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock private CadastrarUsuarioUseCase cadastrarUseCase;
    @Mock private AtualizarUsuarioUseCase atualizarUseCase;
    @Mock private ExcluirUsuarioUseCase excluirUseCase;
    @Mock private BuscarUsuarioUseCase buscarUseCase;
    @Mock private TrocarSenhaUseCase trocarSenhaUseCase;
    @Mock private AutenticarUsuarioUseCase autenticarUseCase;

    private UsuarioController controller;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        controller = new UsuarioController(
                cadastrarUseCase, atualizarUseCase, excluirUseCase,
                buscarUseCase, trocarSenhaUseCase, autenticarUseCase
        );
        usuario = Cliente.builder()
                .id(1L).nome("João Silva").email("joao@email.com").login("joao")
                .tipoUsuario(TipoUsuario.builder().id(1L).nome("CLIENTE").build())
                .build();
    }

    @Test
    void deveDelegarCadastrar() {
        var command = new CadastrarUsuarioUseCase.Command(
                "João Silva", "joao@email.com", "joao", "senha123", 1L,
                "Rua A", "1", null, "Bairro", "Cidade", "SP", "00000-000"
        );
        when(cadastrarUseCase.execute(command)).thenReturn(usuario);

        var resultado = controller.cadastrar(command);

        assertThat(resultado.getNome()).isEqualTo("João Silva");
        verify(cadastrarUseCase).execute(command);
    }

    @Test
    void deveDelegarAtualizar() {
        var command = new AtualizarUsuarioUseCase.Command(
                "João Atualizado", "novo@email.com",
                "Rua B", "2", null, "Bairro", "Cidade", "SP", "00000-000"
        );
        when(atualizarUseCase.execute(1L, command)).thenReturn(usuario);

        var resultado = controller.atualizar(1L, command);

        assertThat(resultado).isEqualTo(usuario);
        verify(atualizarUseCase).execute(1L, command);
    }

    @Test
    void deveDelegarExcluir() {
        controller.excluir(1L);

        verify(excluirUseCase).execute(1L);
    }

    @Test
    void deveDelegarBuscarPorId() {
        when(buscarUseCase.buscarPorId(1L)).thenReturn(usuario);

        var resultado = controller.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(buscarUseCase).buscarPorId(1L);
    }

    @Test
    void deveDelegarBuscarPorNome() {
        when(buscarUseCase.buscarPorNome("João")).thenReturn(List.of(usuario));

        var resultado = controller.buscarPorNome("João");

        assertThat(resultado).hasSize(1);
        verify(buscarUseCase).buscarPorNome("João");
    }

    @Test
    void deveDelegarTrocarSenha() {
        var command = new TrocarSenhaUseCase.Command("velha123", "nova456");
        controller.trocarSenha(1L, command);

        verify(trocarSenhaUseCase).execute(1L, command);
    }

    @Test
    void deveDelegarAutenticar() {
        var command = new AutenticarUsuarioUseCase.Command("joao", "senha123");
        when(autenticarUseCase.execute(command)).thenReturn(usuario);

        var resultado = controller.autenticar(command);

        assertThat(resultado).isEqualTo(usuario);
        verify(autenticarUseCase).execute(command);
    }
}
