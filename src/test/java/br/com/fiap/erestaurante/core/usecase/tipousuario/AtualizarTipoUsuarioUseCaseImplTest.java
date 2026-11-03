package br.com.fiap.erestaurante.core.usecase.tipousuario;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioJaCadastradoException;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarTipoUsuarioUseCaseImplTest {

    @Mock
    private TipoUsuarioGateway gateway;

    private AtualizarTipoUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AtualizarTipoUsuarioUseCaseImpl(gateway);
    }

    @Test
    void deveAtualizarTipoUsuarioComSucesso() {
        var existente = TipoUsuario.builder().id(1L).nome("CLIENTE").build();
        var command = new AtualizarTipoUsuarioUseCase.Command("CLIENTE_VIP");
        var atualizado = TipoUsuario.builder().id(1L).nome("CLIENTE_VIP").build();

        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(gateway.existePorNome("CLIENTE_VIP")).thenReturn(false);
        when(gateway.salvar(any())).thenReturn(atualizado);

        TipoUsuario resultado = useCase.execute(1L, command);

        assertThat(resultado.getNome()).isEqualTo("CLIENTE_VIP");
    }

    @Test
    void devePermitirMesmoNomeSemConflito() {
        var existente = TipoUsuario.builder().id(1L).nome("CLIENTE").build();
        var command = new AtualizarTipoUsuarioUseCase.Command("CLIENTE");

        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(gateway.salvar(any())).thenReturn(existente);

        useCase.execute(1L, command);

        verify(gateway, never()).existePorNome("CLIENTE");
    }

    @Test
    void deveLancarExcecaoSeIdNaoEncontrado() {
        var command = new AtualizarTipoUsuarioUseCase.Command("NOVO");

        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, command))
                .isInstanceOf(TipoUsuarioNaoEncontradoException.class);

        verify(gateway, never()).salvar(any());
    }

    @Test
    void deveLancarExcecaoSeNovoNomeJaExiste() {
        var existente = TipoUsuario.builder().id(1L).nome("CLIENTE").build();
        var command = new AtualizarTipoUsuarioUseCase.Command("DONO_RESTAURANTE");

        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(gateway.existePorNome("DONO_RESTAURANTE")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(1L, command))
                .isInstanceOf(TipoUsuarioJaCadastradoException.class)
                .hasMessageContaining("DONO_RESTAURANTE");

        verify(gateway, never()).salvar(any());
    }
}
