package br.com.fiap.erestaurante.core.usecase.restaurante;

import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.RestauranteNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
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
class ExcluirRestauranteUseCaseImplTest {

    @Mock
    private RestauranteGateway gateway;

    private ExcluirRestauranteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ExcluirRestauranteUseCaseImpl(gateway);
    }

    @Test
    void deveExcluirRestauranteComSucesso() {
        var restaurante = Restaurante.builder()
                .id(1L).nome("Cantina").tipoCozinha("Italiana")
                .dono(DonoRestaurante.builder().id(1L).build())
                .build();

        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(restaurante));
        doNothing().when(gateway).excluir(1L);

        useCase.execute(1L);

        verify(gateway).excluir(1L);
    }

    @Test
    void deveLancarExcecaoSeRestauranteNaoEncontrado() {
        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L))
                .isInstanceOf(RestauranteNaoEncontradoException.class)
                .hasMessageContaining("99");

        verify(gateway, never()).excluir(99L);
    }
}
