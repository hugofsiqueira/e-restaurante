package br.com.fiap.erestaurante.core.usecase.tipousuario;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarTipoUsuarioUseCaseImplTest {

    @Mock
    private TipoUsuarioGateway gateway;

    private BuscarTipoUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new BuscarTipoUsuarioUseCaseImpl(gateway);
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        var tipo = TipoUsuario.builder().id(1L).nome("CLIENTE").build();
        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(tipo));

        TipoUsuario resultado = useCase.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("CLIENTE");
    }

    @Test
    void deveLancarExcecaoSeTipoNaoEncontrado() {
        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.buscarPorId(99L))
                .isInstanceOf(TipoUsuarioNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveBuscarTodosRetornandoLista() {
        var tipos = List.of(
                TipoUsuario.builder().id(1L).nome("CLIENTE").build(),
                TipoUsuario.builder().id(2L).nome("DONO_RESTAURANTE").build()
        );
        when(gateway.buscarTodos()).thenReturn(tipos);

        List<TipoUsuario> resultado = useCase.buscarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(TipoUsuario::getNome)
                .containsExactlyInAnyOrder("CLIENTE", "DONO_RESTAURANTE");
    }
}
