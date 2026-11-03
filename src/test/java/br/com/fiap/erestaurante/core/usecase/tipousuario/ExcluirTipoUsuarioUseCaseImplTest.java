package br.com.fiap.erestaurante.core.usecase.tipousuario;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcluirTipoUsuarioUseCaseImplTest {

    @Mock
    private TipoUsuarioGateway gateway;

    private ExcluirTipoUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ExcluirTipoUsuarioUseCaseImpl(gateway);
    }

    @Test
    void deveExcluirTipoUsuarioComSucesso() {
        var tipo = TipoUsuario.builder().id(1L).nome("CLIENTE").build();
        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(tipo));
        doNothing().when(gateway).excluir(1L);

        useCase.execute(1L);

        verify(gateway).excluir(1L);
    }

    @Test
    void deveLancarExcecaoSeIdNaoEncontrado() {
        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L))
                .isInstanceOf(TipoUsuarioNaoEncontradoException.class)
                .hasMessageContaining("99");

        verify(gateway, never()).excluir(99L);
    }
}
