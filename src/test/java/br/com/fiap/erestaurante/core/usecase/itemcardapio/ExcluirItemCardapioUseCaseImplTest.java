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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcluirItemCardapioUseCaseImplTest {

    @Mock
    private ItemCardapioGateway gateway;

    private ExcluirItemCardapioUseCaseImpl useCase;

    private ItemCardapio item;

    @BeforeEach
    void setUp() {
        useCase = new ExcluirItemCardapioUseCaseImpl(gateway);

        item = ItemCardapio.builder()
                .id(1L).nome("Pizza Margherita")
                .preco(new BigDecimal("35.90")).disponivel(true)
                .restaurante(Restaurante.builder().id(1L).nome("Cantina da Maria").build())
                .build();
    }

    @Test
    void deveExcluirItemComSucesso() {
        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(item));

        useCase.execute(1L);

        verify(gateway).excluir(1L);
    }

    @Test
    void deveLancarExcecaoSeItemNaoEncontrado() {
        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L))
                .isInstanceOf(ItemCardapioNaoEncontradoException.class);

        verify(gateway, never()).excluir(any(Long.class));
    }
}
