package br.com.fiap.erestaurante.core.usecase.itemcardapio;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.ItemCardapioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.ItemCardapioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarItemCardapioUseCaseImplTest {

    @Mock
    private ItemCardapioGateway gateway;

    private BuscarItemCardapioUseCaseImpl useCase;

    private ItemCardapio item;

    @BeforeEach
    void setUp() {
        useCase = new BuscarItemCardapioUseCaseImpl(gateway);

        item = ItemCardapio.builder()
                .id(1L).nome("Pizza Margherita").descricao("Clássica pizza italiana")
                .preco(new BigDecimal("35.90")).disponivel(true)
                .restaurante(Restaurante.builder().id(1L).nome("Cantina da Maria").build())
                .build();
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(item));

        var resultado = useCase.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Pizza Margherita");
    }

    @Test
    void deveLancarExcecaoSeItemNaoEncontradoPorId() {
        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.buscarPorId(99L))
                .isInstanceOf(ItemCardapioNaoEncontradoException.class);
    }

    @Test
    void deveBuscarPorRestauranteComSucesso() {
        when(gateway.buscarPorRestaurante(1L)).thenReturn(List.of(item));

        var resultado = useCase.buscarPorRestaurante(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Pizza Margherita");
    }

    @Test
    void deveRetornarListaVaziaSeRestauranteSemItens() {
        when(gateway.buscarPorRestaurante(2L)).thenReturn(List.of());

        var resultado = useCase.buscarPorRestaurante(2L);

        assertThat(resultado).isEmpty();
    }
}
