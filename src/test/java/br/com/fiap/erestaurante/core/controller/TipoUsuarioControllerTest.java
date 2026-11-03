package br.com.fiap.erestaurante.core.controller;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.usecase.tipousuario.AtualizarTipoUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.tipousuario.BuscarTipoUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.tipousuario.CriarTipoUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.tipousuario.ExcluirTipoUsuarioUseCase;
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
class TipoUsuarioControllerTest {

    @Mock private CriarTipoUsuarioUseCase criarUseCase;
    @Mock private BuscarTipoUsuarioUseCase buscarUseCase;
    @Mock private AtualizarTipoUsuarioUseCase atualizarUseCase;
    @Mock private ExcluirTipoUsuarioUseCase excluirUseCase;

    private TipoUsuarioController controller;

    private TipoUsuario tipo;

    @BeforeEach
    void setUp() {
        controller = new TipoUsuarioController(criarUseCase, buscarUseCase, atualizarUseCase, excluirUseCase);
        tipo = TipoUsuario.builder().id(1L).nome("CLIENTE").build();
    }

    @Test
    void deveDelegarCriar() {
        var command = new CriarTipoUsuarioUseCase.Command("CLIENTE");
        when(criarUseCase.execute(command)).thenReturn(tipo);

        var resultado = controller.criar(command);

        assertThat(resultado.getNome()).isEqualTo("CLIENTE");
        verify(criarUseCase).execute(command);
    }

    @Test
    void deveDelegarBuscarPorId() {
        when(buscarUseCase.buscarPorId(1L)).thenReturn(tipo);

        var resultado = controller.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(buscarUseCase).buscarPorId(1L);
    }

    @Test
    void deveDelegarBuscarTodos() {
        when(buscarUseCase.buscarTodos()).thenReturn(List.of(tipo));

        var resultado = controller.buscarTodos();

        assertThat(resultado).hasSize(1);
        verify(buscarUseCase).buscarTodos();
    }

    @Test
    void deveDelegarAtualizar() {
        var command = new AtualizarTipoUsuarioUseCase.Command("CLIENTE_VIP");
        when(atualizarUseCase.execute(1L, command)).thenReturn(tipo);

        var resultado = controller.atualizar(1L, command);

        assertThat(resultado).isEqualTo(tipo);
        verify(atualizarUseCase).execute(1L, command);
    }

    @Test
    void deveDelegarExcluir() {
        controller.excluir(1L);

        verify(excluirUseCase).execute(1L);
    }
}
